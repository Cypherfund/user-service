package com.cypherfund.campaign.user.services.paymentProcess;


import com.cypherfund.campaign.user.model.ApiResponse;
import com.cypherfund.campaign.user.model.CryptoPaymentRequest;
import com.cypherfund.campaign.user.model.PaymentResponse;
import com.cypherfund.campaign.user.model.RechargeRequest;
import com.cypherfund.campaign.user.proxies.PaymentFeignClient;
import com.cypherfund.campaign.user.utils.Enumerations;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Log4j2
@Service
@RequiredArgsConstructor
public class CryptoPaymentProcess implements IPaymentProcess {
    private final PaymentFeignClient paymentFeignClient;
    @Value(value = "${app.payment.callback.host}")
    private String callbackUrl;
    @Override
    public PaymentResponse recharge(RechargeRequest rechargeRequest, String reference) {
        PaymentResponse paymentResponse = null;
        CryptoPaymentRequest cryptoPaymentRequest = new CryptoPaymentRequest();
        cryptoPaymentRequest.setAmt(rechargeRequest.getAmount());
        cryptoPaymentRequest.setCurrency("USD"); //TODO get default currency
        cryptoPaymentRequest.setDesc("Recharge with reference " + rechargeRequest.getReference() );
        cryptoPaymentRequest.setCode(rechargeRequest.getPaymentCode());
        cryptoPaymentRequest.setMethod(rechargeRequest.getPaymentMethod().name());
        cryptoPaymentRequest.setPriceType(Enumerations.COINBASE_PRICE_TYPE.fixed_price);
        cryptoPaymentRequest.setCallbackUrl(callbackUrl);
        cryptoPaymentRequest.setRef(reference);

        ApiResponse<PaymentResponse> paymentResult =  paymentFeignClient.payByCrypto(cryptoPaymentRequest);
        log.info("response from making payment with reference {} by user {} : {}", rechargeRequest.getReference(), rechargeRequest.getUserId(), paymentResult);

        if (paymentResult != null && paymentResult.getSuccess()) {
            paymentResponse = paymentResult.getData();
            paymentResponse.setStatus(Enumerations.PAYMENT_STATUS.PENDING);
        }

        return paymentResponse;
    }

    @Override
    public Enumerations.PaymentMethod getPaymentMethod() {
        return Enumerations.PaymentMethod.CRYPTO_WALLET;
    }
}
