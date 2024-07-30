package com.cypherfund.campaign.user.exceptions;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

/**
 *
 * @author ngaielizabeth
 */
@Data
public class HttpErrorInfo {

    private LocalDateTime timestamp;
    private String path;
    private HttpStatus httpStatus;
    private String message;
    private String errorCode;

    public HttpErrorInfo(HttpStatus httpStatus, String path, String message) {
        timestamp = LocalDateTime.now();
        this.httpStatus = httpStatus;
        this.path = path;
        this.message = message;
    }


}
