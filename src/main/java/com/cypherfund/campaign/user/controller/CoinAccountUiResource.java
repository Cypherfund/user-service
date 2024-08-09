package com.cypherfund.campaign.user.controller;

import com.cypherfund.campaign.user.dto.TAccountBalanceDto;
import com.cypherfund.campaign.user.model.ApiResponse;
import com.cypherfund.campaign.user.model.DebitRequest;
import com.cypherfund.campaign.user.model.RechargeCoinRequest;
import com.cypherfund.campaign.user.model.RechargeRequest;
import com.cypherfund.campaign.user.services.CoinService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("coin")
public class CoinAccountUiResource {
    @Autowired
    CoinService coinService;

    @PostMapping("/credit")
    public ResponseEntity<ApiResponse<String>> creditWinning(
                                @RequestParam("amount") double amount,
                                @RequestParam("userId") String userId,
                                @RequestParam("reference") String reference) {
        log.info("Crediting coin balance for user: {}", userId);
        coinService.coinReward( userId, amount, reference);
        return ResponseEntity.ok(ApiResponse.success("Winning credited successfully", null));
    }

    @PostMapping("/debit")
    public ResponseEntity<ApiResponse<String>> play(@RequestBody @Valid DebitRequest debitRequest) {
        log.info("Debiting coin balance for user: {}", debitRequest.getUserId());
        coinService.debitCoinBalance( debitRequest);
        return ResponseEntity.ok(ApiResponse.success("Play debited successfully", null));
    }

    @PostMapping("/deposit")
    public ResponseEntity<ApiResponse<TAccountBalanceDto>> credit(@RequestBody @Valid RechargeCoinRequest rechargeRequest) {
        return ResponseEntity.ok(ApiResponse.success("Account credited successfully", coinService.creditCoinBalance(rechargeRequest)));
    }

    @PostMapping("/status")
    public ResponseEntity<ApiResponse<String>> checkStatus(@RequestParam("reference") String reference) {
        return ResponseEntity.ok(ApiResponse.success("Status checked successfully", coinService.checkStatus(reference)));
    }


}
