package com.cypherfund.campaign.user.services;

import com.cypherfund.campaign.user.dal.entity.*;
import com.cypherfund.campaign.user.dal.repository.*;
import com.cypherfund.campaign.user.dto.TAccountBalanceDto;
import com.cypherfund.campaign.user.exceptions.AppException;
import com.cypherfund.campaign.user.model.DebitRequest;
import com.cypherfund.campaign.user.model.PaymentResponse;
import com.cypherfund.campaign.user.model.RechargeCoinRequest;
import com.cypherfund.campaign.user.security.UserPrincipal;
import com.cypherfund.campaign.user.services.paymentProcess.IPaymentProcess;
import com.cypherfund.campaign.user.utils.Enumerations;
import com.cypherfund.campaign.user.utils.ErrorConstants;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import static com.cypherfund.campaign.user.utils.Enumerations.PAYMENT_STATUS.PENDING;
import static com.cypherfund.campaign.user.utils.Enumerations.TRANSACTION_TYPE.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class CoinService {
    private final ModelMapper modelMapper;
    private final TUserRepository tUserRepository;
    private final TAccountBalanceRepository tAccountBalanceRepository;
    private final TransactionRepository transactionRepository;
    private final TTraceRepository traceRepository;
    private final TTraceStatusRepository traceStatusRepository;
    private final Map<Enumerations.PaymentMethod, IPaymentProcess> paymentProcesses;

    @Transactional
    public TAccountBalanceDto coinReward(String userId, double amount, String reference) {
        log.info("Rewarding coin balance for user: {}", userId);

        TUser user = tUserRepository.findById(userId).orElseThrow(() -> new AppException("User not found"));
        TAccountBalance accountBalance = tAccountBalanceRepository.findByLgUserId(userId).orElseGet(() -> {
            TAccountBalance newAccountBalance = new TAccountBalance();
            newAccountBalance.setLgUserId(userId);
            newAccountBalance.setDCurBalance(BigDecimal.ZERO);
            newAccountBalance.setDWinBalance(BigDecimal.ZERO);
            newAccountBalance.setDbCoinBalance(BigDecimal.ZERO);
            newAccountBalance.setDtCreated(Instant.now());
            return tAccountBalanceRepository.save(newAccountBalance);
        });

        accountBalance.setDbCoinBalance(accountBalance.getDbCoinBalance().add(BigDecimal.valueOf(amount)));

        tAccountBalanceRepository.save(accountBalance);

        createTransaction(userId, COIN_REWARD, amount, reference);

        return modelMapper.map(accountBalance, TAccountBalanceDto.class);
    }

    public TAccountBalanceDto creditCoinBalance(RechargeCoinRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        String userId = ((UserPrincipal) authentication.getPrincipal()).getId();

        log.info("Recharging coin balance for user: {}", userId);

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

        if (paymentResponse.getStatus().equals(Enumerations.PAYMENT_STATUS.SUCCESS)) {
            return updateUserBalance(userId, request.getAmount(), request.getReference(), Enumerations.TRANSACTION_TYPE.COIN_PURCHASE);
        } else if (paymentResponse.getStatus().equals(PENDING)){
            log.info("payment response: {}", paymentResponse);
            return null;
        }else {
            throw new AppException("payment failed");
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
            updateUserBalance(trace.getLgUserId(), trace.getDbAmount(), trace.getStrOriginatingTransaction(), Enumerations.TRANSACTION_TYPE.COIN_PURCHASE);
            return Enumerations.PAYMENT_STATUS.SUCCESS.name();
        } else if (traceStatus.getStrStatus().equalsIgnoreCase(Enumerations.PAYMENT_STATUS.FAILED.name())) {
            return Enumerations.PAYMENT_STATUS.FAILED.name();
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

        accountBalance.setDbCoinBalance(accountBalance.getDbCoinBalance().add(BigDecimal.valueOf(amount)));

        accountBalance = tAccountBalanceRepository.save(accountBalance);

        createTransaction(userId, transactionType, amount, reference);

        return modelMapper.map(accountBalance, TAccountBalanceDto.class);
    }

    @Transactional
    public TAccountBalanceDto debitCoinBalance(DebitRequest debitRequest) {
        log.info("Debiting coin balance for user: {}", debitRequest.getUserId());

        tUserRepository.findById(debitRequest.getUserId()).orElseThrow(() -> new AppException("User not found"));
        TAccountBalance accountBalance = tAccountBalanceRepository.findByLgUserId(debitRequest.getUserId())
                .orElseThrow(() -> new AppException("Account balance not found"));

        if (accountBalance.getDbCoinBalance().compareTo(BigDecimal.valueOf(debitRequest.getAmount())) < 0)
            throw new AppException("Insufficient balance");

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


    private TAccountBalance createNewAccount(String userId) {
        TAccountBalance newAccountBalance = new TAccountBalance();
        newAccountBalance.setLgUserId(userId);
        newAccountBalance.setDCurBalance(BigDecimal.ZERO);
        newAccountBalance.setDWinBalance(BigDecimal.ZERO);
        newAccountBalance.setDtCreated(Instant.now());
        newAccountBalance.setDtUpdated(Instant.now());
        return tAccountBalanceRepository.save(newAccountBalance);
    }

    private TTrace createTrace(RechargeCoinRequest request) {
        TTrace tTrace = new TTrace();
        tTrace.setLgTraceId(UUID.randomUUID().toString());
        tTrace.setDtDateCreated(new Date().toInstant());
        tTrace.setStrProviderCode(request.getPaymentCode());
        tTrace.setDbAmount(request.getCoinAmount());
        tTrace.setStrPhoneNumber(request.getExtra());
        tTrace.setStrOriginatingTransaction(request.getReference());
        tTrace.setLgUserId(request.getUserId());
        tTrace.setStrType(Enumerations.TRANSACTION_TYPE.COIN_PURCHASE.name());

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
