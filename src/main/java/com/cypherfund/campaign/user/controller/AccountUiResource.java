package com.cypherfund.campaign.user.controller;

import com.cypherfund.campaign.user.dal.entity.Transaction;
import com.cypherfund.campaign.user.dto.TAccountBalanceDto;
import com.cypherfund.campaign.user.model.ApiResponse;
import com.cypherfund.campaign.user.model.CallbackResponse;
import com.cypherfund.campaign.user.model.DebitRequest;
import com.cypherfund.campaign.user.model.RechargeRequest;
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
    public ResponseEntity<ApiResponse<String>> creditWinning(
                                @RequestParam("amount") double amount,
                                @RequestParam("userId") String userId,
                                @RequestParam("reference") String reference) {
        log.info("Crediting winning balance for user: {}", userId);
        accountService.creditWinningBalance( userId, amount, reference);
        return ResponseEntity.ok(ApiResponse.success("Winning credited successfully", null));
    }

    @PostMapping("/play")
    public ResponseEntity<ApiResponse<TAccountBalanceDto>> play(@RequestBody @Valid DebitRequest debitRequest) {
        log.info("Debiting play balance for user: {}", debitRequest.getUserId());
        TAccountBalanceDto  accountBalanceDto = accountService.debitBalance( debitRequest);
        return ResponseEntity.ok(ApiResponse.success("Play debited successfully", accountBalanceDto));
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TAccountBalanceDto>> credit(@RequestBody @Valid RechargeRequest rechargeRequest) {
        return ResponseEntity.ok(ApiResponse.success("Account credited successfully", accountService.depositCurrentAccount(rechargeRequest)));
    }

    @PostMapping("/callback")
    public void callback(@RequestBody CallbackResponse callback) {
        accountService.processCallback(callback);
        ResponseEntity.ok();
    }

    @PostMapping("/status")
    public ResponseEntity<ApiResponse<String>> checkStatus(@RequestParam("reference") String reference) {
        return ResponseEntity.ok(ApiResponse.success("Status checked successfully", accountService.checkStatus(reference)));
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
