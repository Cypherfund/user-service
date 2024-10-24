package com.cypherfund.campaign.user.services;

import com.cypherfund.campaign.user.dal.entity.*;
import com.cypherfund.campaign.user.dal.repository.*;
import com.cypherfund.campaign.user.dto.SendNotificationDto;
import com.cypherfund.campaign.user.dto.TAccountBalanceDto;
import com.cypherfund.campaign.user.exceptions.AppException;
import com.cypherfund.campaign.user.model.payment.CallbackResponse;
import com.cypherfund.campaign.user.model.payment.DebitRequest;
import com.cypherfund.campaign.user.model.payment.PaymentResponse;
import com.cypherfund.campaign.user.model.payment.RechargeRequest;
import com.cypherfund.campaign.user.proxies.NotificationFeignClient;
import com.cypherfund.campaign.user.security.UserPrincipal;
import com.cypherfund.campaign.user.services.paymentProcess.IPaymentProcess;
import com.cypherfund.campaign.user.utils.Enumerations;
import com.cypherfund.campaign.user.utils.ErrorConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.commons.lang.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.aop.framework.AopContext;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.cypherfund.campaign.user.utils.Enumerations.NOTIFICATION_TYPE.PAYMENT;
import static com.cypherfund.campaign.user.utils.Enumerations.PAYMENT_STATUS.*;
import static com.cypherfund.campaign.user.utils.Enumerations.TRANSACTION_TYPE.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class AccountService {
    private final ModelMapper modelMapper;
    private final TUserRepository tUserRepository;
    private final TAccountBalanceRepository tAccountBalanceRepository;
    private final TransactionRepository transactionRepository;
    private final TTraceRepository traceRepository;
    private final TTraceStatusRepository traceStatusRepository;
    private final Map<Enumerations.PaymentMethod, IPaymentProcess> paymentProcesses;
    private final CoinService coinService;
    private final RestTemplate restTemplate;

    private final NotificationFeignClient notificationFeignClient;


    @Transactional
    public void creditWinningBalance(String userId, double amount, String reference) {
        log.info("Crediting winning balance for user: {}", userId);

        TUser user = tUserRepository.findById(userId).orElseThrow(() -> new AppException("User not found"));
        TAccountBalance accountBalance = tAccountBalanceRepository.findByLgUserId(userId).orElseGet(() -> {
            TAccountBalance newAccountBalance = new TAccountBalance();
            newAccountBalance.setLgUserId(userId);
            newAccountBalance.setDCurBalance(BigDecimal.ZERO);
            newAccountBalance.setDWinBalance(BigDecimal.ZERO);
            newAccountBalance.setDtCreated(Instant.now());
            return tAccountBalanceRepository.save(newAccountBalance);
        });

        accountBalance.setDWinBalance(accountBalance.getDWinBalance().add(BigDecimal.valueOf(amount)));

        tAccountBalanceRepository.save(accountBalance);

        createTransaction(userId, BB_WINNING, amount, reference);
    }

    @Transactional
    public void debitWinningBalance(String userId, double amount, String reference) {
        log.info("Debiting winning balance for user: {}", userId);

        TUser user = tUserRepository.findById(userId).orElseThrow(() -> new AppException("User not found"));
        TAccountBalance accountBalance = tAccountBalanceRepository.findByLgUserId(userId)
                .orElseThrow(() -> new AppException("Account balance not found"));

        if (accountBalance.getDWinBalance().compareTo(BigDecimal.valueOf(amount)) < 0)
            throw new AppException("Insufficient balance");
        accountBalance.setDWinBalance(accountBalance.getDWinBalance().subtract(BigDecimal.valueOf(amount)));

        tAccountBalanceRepository.save(accountBalance);

        createTransaction(userId, WITHDRAWAL, amount, reference);
    }

    public TAccountBalanceDto depositCurrentAccount(RechargeRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userId = ((UserPrincipal) authentication.getPrincipal()).getId();

        log.info("Recharging account for user: {}", userId);

        tUserRepository.findById(userId).orElseThrow(() -> new AppException("User not found"));

        if (traceRepository.existsByStrOriginatingTransaction(request.getReference())) {
            ErrorConstants.ErrorConstantsEnum errorConstantsEnum = ErrorConstants.ErrorConstantsEnum.CONFLICT;
            errorConstantsEnum.setMessage("Reference already used");
            throw new AppException(errorConstantsEnum);
        }

        log.info("Creating trace");
        TTrace trace = createTrace(request);

        IPaymentProcess paymentProcessor = paymentProcesses.get(request.getPaymentMethod());

        if (paymentProcessor == null) {
            throw new AppException("payment method not supported");
        }

        PaymentResponse paymentResponse = paymentProcessor.recharge(request, trace.getLgTraceId());

        if (paymentResponse == null) {
            throw new AppException("Impossible to initiate payment");
        }

        this.createTraceStatus(PENDING.name(), paymentResponse.getTransactionId(), "Payment request sent to payment service", trace.getLgTraceId());

        if (paymentResponse.getStatus().equals(SUCCESS)) {
            return updateUserBalance(userId, request.getAmount(), request.getReference(), DEPOSIT);
        } else if (paymentResponse.getStatus().equals(PENDING)){
            log.info("payment response: {}", paymentResponse);
            return null;
        }else {
            throw new AppException("payment failed");
        }
    }

    public void processCallback(CallbackResponse callbackResponse) {
        TTraceStatus traceStatus = traceStatusRepository.findByStrExternalTransaction(callbackResponse.getTransactionId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new AppException("Invalid transaction id"));

        TTrace trace = traceRepository.findById(traceStatus.getLgTraceId())
                .orElseThrow(() -> new AppException("Invalid trace id"));

        boolean isPaymentSuccessful = callbackResponse.getStatus().equalsIgnoreCase(SUCCESS.name());

        parseCallbackResponse(callbackResponse, trace, isPaymentSuccessful);

        new Thread(() -> sendPaymentNotification(trace, isPaymentSuccessful)).start();

        if (!StringUtils.isBlank(trace.getCallbackUrl())) {
            //execute in another thread
            new Thread(() -> sendCallback(callbackResponse, trace, isPaymentSuccessful)).start();
        }
    }

    private void sendPaymentNotification(TTrace trace, boolean isPaymentSuccessful) {
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("status", isPaymentSuccessful ? SUCCESS.name() : FAILED.name());
            jsonObject.put("transactionId", trace.getLgTraceId());
            jsonObject.put("message", isPaymentSuccessful ? "Payment successful" : "Payment failed");
            jsonObject.put("type", PAYMENT.name());

            SendNotificationDto sendNotificationDto = SendNotificationDto.builder()
                    .message(jsonObject.toString())
                    .title("")
                    .channel("REALTIME")
                    .destination(new String[]{"/topic/payment-status/" + trace.getLgUserId()})
                    .build();

            log.info("Sending notification {} for user {} ", sendNotificationDto, trace.getLgUserId());

            notificationFeignClient.sendEmailNotification(sendNotificationDto);
        } catch (Exception ex) {
            log.error("error sending notification for internation account {}", ex.getMessage());
        }
    }

    @Transactional
    public void parseCallbackResponse(CallbackResponse callbackResponse, TTrace trace, boolean isPaymentSuccessful) {
        if (isPaymentSuccessful) {
            createTraceStatus(SUCCESS.name(), callbackResponse.getTransactionId(), "Payment successful", trace.getLgTraceId());
            String transactionType = trace.getStrType();
            if (transactionType.equalsIgnoreCase(COIN_PURCHASE.name())) {
                this.coinService.updateUserBalance(trace.getLgUserId(), trace.getDbAmount(), trace.getStrOriginatingTransaction(), COIN_PURCHASE);
            } else {
                updateUserBalance(trace.getLgUserId(), trace.getDbAmount(), trace.getStrOriginatingTransaction(), DEPOSIT);
            }
        } else {
            createTraceStatus(FAILED.name(), callbackResponse.getTransactionId(), "Payment failed", trace.getLgTraceId());
        }
    }

    @SneakyThrows
    private void sendCallback(CallbackResponse callbackResponse, TTrace trace, boolean isPaymentSuccessful) {
        log.info("Sending callback response: {}", callbackResponse);
        PaymentResponse response = new PaymentResponse();
        response.setStatus(isPaymentSuccessful ? SUCCESS : FAILED);
        response.setTransactionId(trace.getStrOriginatingTransaction());
        response.setData(callbackResponse);

        String url = trace.getCallbackUrl();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        String jsonResponse = new ObjectMapper().writeValueAsString(response);

        HttpEntity<String> entity = new HttpEntity<>(jsonResponse, headers);

        ResponseEntity<String> responseEntity = restTemplate.postForEntity(url, entity, String.class);
        // Log the status of the response
        log.info("Callback response status: {}", responseEntity.getStatusCode());
    }

    public String checkStatus(String reference) {
        TTrace trace = traceRepository.findByStrOriginatingTransaction(reference)
                .orElseThrow(() -> new AppException("Invalid reference"));

        TTraceStatus traceStatus = traceStatusRepository.findFirstByLgTraceIdOrderByDtDateCreatedDesc(trace.getLgTraceId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new AppException("Invalid trace id"));

        if (traceStatus.getStrStatus().equalsIgnoreCase(SUCCESS.name())) {
            updateUserBalance(trace.getLgUserId(), trace.getDbAmount(), trace.getStrOriginatingTransaction(), DEPOSIT);
            return SUCCESS.name();
        } else if (traceStatus.getStrStatus().equalsIgnoreCase(FAILED.name())) {
            return FAILED.name();
        } else {
            return Enumerations.PAYMENT_STATUS.PENDING.name();
        }


    }

    @Transactional
    public TAccountBalanceDto updateUserBalance(String userId, double amount, String reference, Enumerations.TRANSACTION_TYPE transactionType) {
        Transaction transaction = transactionRepository.findByReferenceAndUserId(reference, userId)
                .stream()
                .findFirst()
                .orElse(null);

        TAccountBalance accountBalance = tAccountBalanceRepository.findByLgUserId(userId).orElseGet(() -> createNewAccount(userId));

        if (transaction != null) {
            return modelMapper.map(accountBalance, TAccountBalanceDto.class);
        }

        accountBalance.setDCurBalance(accountBalance.getDCurBalance().add(BigDecimal.valueOf(amount)));

        accountBalance = tAccountBalanceRepository.save(accountBalance);

        createTransaction(userId, transactionType, amount, reference);

        return modelMapper.map(accountBalance, TAccountBalanceDto.class);
    }

    @Transactional
    public TAccountBalanceDto debitBalance(DebitRequest debitRequest) {
        log.info("Debiting balance for user: {}", debitRequest.getUserId());

        TUser user = tUserRepository.findById(debitRequest.getUserId()).orElseThrow(() -> new AppException("User not found"));
        TAccountBalance accountBalance = tAccountBalanceRepository.findByLgUserId(debitRequest.getUserId())
                .orElseThrow(() -> new AppException("Account balance not found"));

        if (accountBalance.getDCurBalance().compareTo(BigDecimal.valueOf(debitRequest.getAmount())) < 0)
            throw new RuntimeException("Insufficient balance");

        accountBalance.setDCurBalance(accountBalance.getDCurBalance().subtract(BigDecimal.valueOf(debitRequest.getAmount())));

        tAccountBalanceRepository.save(accountBalance);

        createTransaction(debitRequest.getUserId(),
                debitRequest.getTransactionType(),
                debitRequest.getAmount(),
                debitRequest.getReference());

        return modelMapper.map(accountBalance, TAccountBalanceDto.class);
    }



    private void createTransaction(String userId, Enumerations.TRANSACTION_TYPE withdrawal, double amount, String reference) {
        Transaction transaction = new Transaction();
        transaction.setUserId(userId);
        transaction.setType(withdrawal);
        transaction.setAmount(BigDecimal.valueOf(amount));
        transaction.setReference(reference);
        transaction.setCreatedAt(Instant.now());

        transactionRepository.save(transaction);
    }


    public TAccountBalanceDto getBalance(String userId) {
        log.info("Retrieving balance for user: {}", userId);

        TAccountBalance accountBalance = tAccountBalanceRepository.findByLgUserId(userId)
                .orElseGet(() -> createNewAccount(userId));

        return modelMapper.map(accountBalance, TAccountBalanceDto.class);
    }

    private TAccountBalance createNewAccount(String userId) {
        TAccountBalance newAccountBalance = new TAccountBalance();
        newAccountBalance.setLgUserId(userId);
        newAccountBalance.setDCurBalance(BigDecimal.ZERO);
        newAccountBalance.setDWinBalance(BigDecimal.ZERO);
        newAccountBalance.setDtCreated(Instant.now());
        newAccountBalance.setDtUpdated(Instant.now());
        return tAccountBalanceRepository.save(newAccountBalance);
    }

    public List<Transaction> getTransactions(String userId, String reference) {
        log.info("Retrieving transactions for user: {}", userId);
        if (!StringUtils.isBlank(reference))
            return transactionRepository.findByReferenceAndUserId(reference, userId);

        return transactionRepository.findByUserId(userId);
    }

    private TTrace createTrace(RechargeRequest request) {
        TTrace tTrace = new TTrace();
        tTrace.setLgTraceId(UUID.randomUUID().toString());
        tTrace.setDtDateCreated(new Date().toInstant());
        tTrace.setStrProviderCode(request.getPaymentCode());
        tTrace.setDbAmount(request.getAmount());
        tTrace.setStrPhoneNumber(request.getExtra());
        tTrace.setStrOriginatingTransaction(request.getReference());
        tTrace.setLgUserId(request.getUserId());
        tTrace.setCallbackUrl(request.getCallbackUrl());
        tTrace.setStrType(Enumerations.TRANSACTION_TYPE.DEPOSIT.name());

        return traceRepository.save(tTrace);
    }

    private void createTraceStatus(String status, String externalTransactionId, String message, String traceId) {
        TTraceStatus tTraceStatus = new TTraceStatus();
        tTraceStatus.setLgTraceId(traceId);
        tTraceStatus.setLgTraceStatus(UUID.randomUUID().toString());
        tTraceStatus.setStrStatus(status);
        tTraceStatus.setStrExternalTransaction(externalTransactionId);
        tTraceStatus.setDtDateCreated(new Date().toInstant());
        tTraceStatus.setStrMsg(message);
        tTraceStatus.setStrExtCode("");

        traceStatusRepository.save(tTraceStatus);
    }
}
