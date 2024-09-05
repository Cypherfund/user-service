package com.cypherfund.campaign.user.services.paymentProcess;

import com.cypherfund.campaign.user.model.payment.PaymentResponse;
import com.cypherfund.campaign.user.model.payment.RechargeRequest;
import com.cypherfund.campaign.user.utils.Enumerations;

public interface IPaymentProcess {
    PaymentResponse recharge(RechargeRequest enrollmentRequest, String reference);

    Enumerations.PaymentMethod getPaymentMethod();
}
