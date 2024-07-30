package com.cypherfund.campaign.user.exceptions;

import com.cypherfund.campaign.user.utils.ErrorConstants.ErrorConstantsEnum;

public class AppException extends RuntimeException {
    private ErrorConstantsEnum errorConstantsEnum;
    public AppException(String message) {
        super(message);
        this.errorConstantsEnum = ErrorConstantsEnum.INTERNAL_SERVER_ERROR;
        this.errorConstantsEnum.setMessage(message);
    }

    public AppException(ErrorConstantsEnum errorConstantsEnum) {
        super(errorConstantsEnum.getMessage());
        this.errorConstantsEnum = errorConstantsEnum;
    }

    public AppException(String message, Throwable cause) {
        super(message, cause);
    }

    public ErrorConstantsEnum getErrorConstantsEnum() {
        return errorConstantsEnum;
    }
}
