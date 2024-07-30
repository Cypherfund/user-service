package com.cypherfund.campaign.user.services.paymentProcess;

import com.cypherfund.campaign.user.model.PaymentResponse;
import com.cypherfund.campaign.user.model.RechargeRequest;
import com.cypherfund.campaign.user.utils.Enumerations;

public interface IPaymentProcess {
    PaymentResponse recharge(RechargeRequest enrollmentRequest, String reference);

    Enumerations.PaymentMethod getPaymentMethod();
}
