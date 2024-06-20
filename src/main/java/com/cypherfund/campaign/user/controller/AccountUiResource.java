package com.cypherfund.campaign.user.controller;

import com.cypherfund.campaign.user.dal.entity.Transaction;
import com.cypherfund.campaign.user.dto.TAccountBalanceDto;
import com.cypherfund.campaign.user.model.ApiResponse;
import com.cypherfund.campaign.user.model.DebitRequest;
import com.cypherfund.campaign.user.services.AccountService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("account")
public class AccountUiResource {
    @Autowired
    AccountService accountService;

    @PostMapping("/winning")
    public ResponseEntity<ApiResponse<String>> creditWinning(@RequestParam("amount") double amount,
                              @RequestParam("userId") String userId,
                              @RequestParam("reference") String reference) {
        log.info("Crediting winning balance for user: {}", userId);
        accountService.creditWinningBalance( userId, amount, reference);
        return ResponseEntity.ok(ApiResponse.success("Winning credited successfully", null));
    }

    @PostMapping("/play")
    public ResponseEntity<ApiResponse<String>> play(@RequestBody @Valid DebitRequest debitRequest) {
        log.info("Debiting play balance for user: {}", debitRequest.getUserId());
        accountService.debitBalance( debitRequest);
        return ResponseEntity.ok(ApiResponse.success("Play debited successfully", null));
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TAccountBalanceDto>> credit(@RequestParam("amount") double amount,
                              @RequestParam("userId") String userId,
                              @RequestParam("reference") String reference) {
        log.info("Crediting account balance for user: {}", userId);
        return ResponseEntity.ok(ApiResponse.success("Account credited successfully", accountService.depositCurrentAccount( userId, amount, reference)));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<String>> withdraw(@RequestParam("amount") double amount,
                              @RequestParam("userId") String userId,
                              @RequestParam("reference") String reference) {
        log.info("Debiting winning balance for user: {}", userId);
        accountService.debitWinningBalance( userId, amount, reference);
        return ResponseEntity.ok(ApiResponse.success("Withdrawal successful", null));
    }

    @GetMapping("/balance/{userId}")
    public ResponseEntity<ApiResponse<TAccountBalanceDto>> getBalance(@PathVariable String userId) {
        log.info("Retrieving balance for user: {}", userId);
        return ResponseEntity.ok(ApiResponse.success("Balance retrieved successfully", accountService.getBalance(userId)));
    }

    @GetMapping("/transactions/{userId}")
    public ResponseEntity<ApiResponse<List<Transaction>>> getTransactions(@PathVariable String userId) {
        log.info("Retrieving transactions for user: {}", userId);
        return ResponseEntity.ok(ApiResponse.success("Transactions retrieved successfully", accountService.getTransactions(userId)));
    }

}
