package com.cypherfund.campaign.user.services;

import com.cypherfund.campaign.user.dal.entity.*;
import com.cypherfund.campaign.user.dal.repository.*;
import com.cypherfund.campaign.user.dto.TAccountBalanceDto;
import com.cypherfund.campaign.user.exceptions.AppException;
import com.cypherfund.campaign.user.model.*;
import com.cypherfund.campaign.user.services.paymentProcess.IPaymentProcess;
import com.cypherfund.campaign.user.utils.Enumerations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.cypherfund.campaign.user.utils.Enumerations.PAYMENT_STATUS.PENDING;
import static com.cypherfund.campaign.user.utils.Enumerations.TRANSACTION_TYPE.BB_WINNING;
import static com.cypherfund.campaign.user.utils.Enumerations.TRANSACTION_TYPE.WITHDRAWAL;

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

        String userId = ((UserDetails) authentication.getPrincipal()).getUsername();

        log.info("Recharging account for user: {}", userId);

        tUserRepository.findById(userId).orElseThrow(() -> new AppException("User not found"));

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

        if (paymentResponse.getStatus().equals(Enumerations.PAYMENT_STATUS.SUCCESS)) {
            return updateUserBalance(userId, request.getAmount(), request.getReference());
        } else if (paymentResponse.getStatus().equals(PENDING)){
            log.info("payment response: {}", paymentResponse);
            return null;
        }else {
            throw new AppException("payment failed");
        }
    }

    @Transactional
    public void processCallback(CallbackResponse callbackResponse) {
        TTraceStatus traceStatus = traceStatusRepository.findByStrExternalTransaction(callbackResponse.getTransactionId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new AppException("Invalid transaction id"));

        TTrace trace = traceRepository.findById(traceStatus.getLgTraceId())
                .orElseThrow(() -> new AppException("Invalid trace id"));

        if (callbackResponse.getStatus().equalsIgnoreCase(Enumerations.PAYMENT_STATUS.SUCCESS.name())) {
            createTraceStatus(Enumerations.PAYMENT_STATUS.SUCCESS.name(), callbackResponse.getTransactionId(), "Payment successful", trace.getLgTraceId());
            updateUserBalance(trace.getLgUserId(), trace.getDbAmount(), trace.getStrOriginatingTransaction());
        } else {
            createTraceStatus(Enumerations.PAYMENT_STATUS.FAILED.name(), callbackResponse.getTransactionId(), "Payment failed", trace.getLgTraceId());
        }
    }

    public String checkStatus(String reference) {
        TTrace trace = traceRepository.findByStrOriginatingTransaction(reference)
                .orElseThrow(() -> new AppException("Invalid reference"));

        TTraceStatus traceStatus = traceStatusRepository.findFirstByLgTraceIdOrderByDtDateCreatedDesc(trace.getLgTraceId())
                .stream()
                .findFirst()
                .orElseThrow(() -> new AppException("Invalid trace id"));

        if (traceStatus.getStrStatus().equalsIgnoreCase(Enumerations.PAYMENT_STATUS.SUCCESS.name())) {
            updateUserBalance(trace.getLgUserId(), trace.getDbAmount(), trace.getStrOriginatingTransaction());
            return Enumerations.PAYMENT_STATUS.SUCCESS.name();
        } else if (traceStatus.getStrStatus().equalsIgnoreCase(Enumerations.PAYMENT_STATUS.FAILED.name())) {
            return Enumerations.PAYMENT_STATUS.FAILED.name();
        } else {
            return Enumerations.PAYMENT_STATUS.PENDING.name();
        }


    }

    @Transactional
    public TAccountBalanceDto updateUserBalance(String userId, double amount, String reference) {
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

        createTransaction(userId, Enumerations.TRANSACTION_TYPE.DEPOSIT, amount, reference);

        return modelMapper.map(accountBalance, TAccountBalanceDto.class);
    }

    @Transactional
    public void debitBalance(DebitRequest debitRequest) {
        log.info("Debiting balance for user: {}", debitRequest.getUserId());

        TUser user = tUserRepository.findById(debitRequest.getUserId()).orElseThrow(() -> new AppException("User not found"));
        TAccountBalance accountBalance = tAccountBalanceRepository.findByLgUserId(debitRequest.getUserId())
                .orElseThrow(() -> new AppException("Account balance not found"));

        if (accountBalance.getDCurBalance().compareTo(BigDecimal.valueOf(debitRequest.getAmount())) < 0)
            throw new AppException("Insufficient balance");

        accountBalance.setDCurBalance(accountBalance.getDCurBalance().subtract(BigDecimal.valueOf(debitRequest.getAmount())));

        tAccountBalanceRepository.save(accountBalance);

        createTransaction(debitRequest.getUserId(),
                debitRequest.getTransactionType(),
                debitRequest.getAmount(),
                debitRequest.getReference());
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

    public List<Transaction> getTransactions(String userId) {
        log.info("Retrieving transactions for user: {}", userId);
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
