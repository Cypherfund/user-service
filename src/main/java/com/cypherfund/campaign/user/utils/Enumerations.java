package com.cypherfund.campaign.user.utils;

public class Enumerations {
    public enum TRANSACTION_TYPE {
        BB_BET, BB_WINNING, WITHDRAWAL, DEPOSIT, SUBSCRIPTION
    }

    public enum COINBASE_PRICE_TYPE {
        fixed_price, no_price
    }

    public enum PAYMENT_STATUS {
        PENDING, FAILED, SUCCESS, CANCELLED
    }
    public enum PaymentMethod {
        CREDIT_CARD, CRYPTO_WALLET, MOBILE_WALLET
    }
}
