package com.cypherfund.campaign.user.services.paymentProcess;


import com.cypherfund.campaign.user.model.*;
import com.cypherfund.campaign.user.model.payment.MobilePaymentDetails;
import com.cypherfund.campaign.user.model.payment.MobilePaymentRequest;
import com.cypherfund.campaign.user.model.payment.PaymentResponse;
import com.cypherfund.campaign.user.model.payment.RechargeRequest;
import com.cypherfund.campaign.user.proxies.PaymentFeignClient;
import com.cypherfund.campaign.user.utils.Enumerations;
import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Author: E.Ngai
 * Date: 3/14/2024
 * Time: 1:54 PM
 **/
@Log4j2
@Service
@RequiredArgsConstructor
public class MobilePaymentProcess implements IPaymentProcess {
    private final PaymentFeignClient paymentFeignClient;
    @Value(value = "${app.payment.callback.host}")
    private String callbackUrl;
    @Override
    public PaymentResponse recharge(RechargeRequest rechargeRequest, String reference) {
        PaymentResponse paymentResponse = null;

        MobilePaymentDetails mobilePaymentDetails = new Gson().fromJson(rechargeRequest.getExtra(), MobilePaymentDetails.class);

        MobilePaymentRequest mobilePaymentRequest = new MobilePaymentRequest();
        mobilePaymentRequest.setAmt(rechargeRequest.getAmount());
        mobilePaymentRequest.setCurrency("USD"); //TODO get default currency
        mobilePaymentRequest.setDesc("Recharge with reference " + rechargeRequest.getReference() );
        mobilePaymentRequest.setCode(rechargeRequest.getPaymentCode());
        mobilePaymentRequest.setMethod(rechargeRequest.getPaymentMethod().name());
        mobilePaymentRequest.setCallbackUrl(callbackUrl);
        mobilePaymentRequest.setRef(reference);
        mobilePaymentRequest.setPhn(mobilePaymentDetails.getPhn());

        ApiResponse<PaymentResponse> paymentResult =  paymentFeignClient.payByMobile(mobilePaymentRequest);
        log.info("response from making payment with reference {} by user {} : {}", rechargeRequest.getReference(), rechargeRequest.getUserId(), paymentResult);

        if (paymentResult != null && paymentResult.getSuccess()) {
            paymentResponse = paymentResult.getData();
            paymentResponse.setStatus(Enumerations.PAYMENT_STATUS.PENDING);
        }

        return paymentResponse;
    }

    @Override
    public Enumerations.PaymentMethod getPaymentMethod() {
        return Enumerations.PaymentMethod.MOBILE_WALLET;
    }
}
