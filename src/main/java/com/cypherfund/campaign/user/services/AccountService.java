package com.cypherfund.campaign.user.services;

import com.cypherfund.campaign.user.dal.entity.TAccountBalance;
import com.cypherfund.campaign.user.dal.entity.TUser;
import com.cypherfund.campaign.user.dal.entity.Transaction;
import com.cypherfund.campaign.user.dal.repository.TAccountBalanceRepository;
import com.cypherfund.campaign.user.dal.repository.TUserRepository;
import com.cypherfund.campaign.user.dal.repository.TransactionRepository;
import com.cypherfund.campaign.user.dto.TAccountBalanceDto;
import com.cypherfund.campaign.user.exceptions.AppException;
import com.cypherfund.campaign.user.model.DebitRequest;
import com.cypherfund.campaign.user.utils.Enumerations;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

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

    @Transactional
    public TAccountBalanceDto depositCurrentAccount(String userId, double amount, String reference) {
        log.info("Crediting balance for user: {}", userId);

        TUser user = tUserRepository.findById(userId).orElseThrow(() -> new AppException("User not found"));
        TAccountBalance accountBalance = tAccountBalanceRepository.findByLgUserId(userId).orElseGet(() -> createNewAccount(userId));

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
}
