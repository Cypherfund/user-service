package com.cypherfund.campaign.user.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;

@NoArgsConstructor
@Setter
@Getter
public class CallbackResponse implements Serializable {
    private String status;
    private String transactionId;
    private Object message;
}
