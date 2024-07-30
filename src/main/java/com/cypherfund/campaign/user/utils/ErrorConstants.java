package com.cypherfund.campaign.user.utils;

import org.springframework.http.HttpStatus;

/**
 * Author: E.Ngai
 * Date: 7/30/2024
 * Time: 11:49 AM
 **/
public class ErrorConstants {
    //create a bunch of error contants and theier codes as enums
    public enum ErrorConstantsEnum {
        INVALID_REQUEST(400, "Invalid request", "400"),
        INTERNAL_SERVER_ERROR(500, "Internal server error", "500"),
        NOT_FOUND(404, "Resource not found", "404"),
        UNAUTHORIZED(401, "Unauthorized", "401"),
        FORBIDDEN(403, "Forbidden", "403"),
        BAD_REQUEST(400, "Bad request",  "400"),
        METHOD_NOT_ALLOWED(405, "Method not allowed", "405"),
        CONFLICT(409, "Conflict", "409"),
        UNSUPPORTED_MEDIA_TYPE(415, "Unsupported media type", "415"),
        UNPROCESSABLE_ENTITY(422, "Unprocessable entity", "422"),
        TOO_MANY_REQUESTS(429, "Too many requests", "429"),
        SERVICE_UNAVAILABLE(503, "Service unavailable", "503");

        private final Integer code;
        private final String errorCode;
        private String message;
        private final HttpStatus httpStatus;

        ErrorConstantsEnum(Integer code, String message, String errorCode) {
            this.code = code;
            this.message = message;
            this.errorCode = errorCode;
            this.httpStatus = HttpStatus.valueOf(code);
        }

        public Integer getCode() {
            return code;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String getErrorCode() {
            return errorCode;
        }

        public HttpStatus getHttpStatus() {
            return httpStatus;
        }

        public String getMessage() {
            return message;
        }
    }
}
