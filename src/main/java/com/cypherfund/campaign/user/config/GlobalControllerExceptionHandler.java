//package com.cypherfund.campaign.user.config;
//
//import com.cypherfund.campaign.exceptions.HttpErrorInfo;
//import com.cypherfund.campaign.exceptions.InvalidInputException;
//import com.cypherfund.campaign.exceptions.NotFoundException;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.bind.annotation.ExceptionHandler;
//import org.springframework.web.bind.annotation.RestControllerAdvice;
//import org.springframework.web.context.request.WebRequest;
//import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
//
//import static org.springframework.http.HttpStatus.*;
//
///**
// *
// * @author ngaielizabeth
// */
//@Slf4j
//@RestControllerAdvice
//public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {
//  @ExceptionHandler(Exception.class)
//  public final ResponseEntity<HttpErrorInfo> handleAllExceptions(Exception ex, WebRequest request) throws Exception {
//    HttpErrorInfo errorDetails = new HttpErrorInfo(INTERNAL_SERVER_ERROR, request.getDescription(false), ex.getMessage());
//    return new ResponseEntity<>(errorDetails, INTERNAL_SERVER_ERROR);
//
//  }
//
//  @ExceptionHandler(NotFoundException.class)
//  public ResponseEntity<HttpErrorInfo> handleNotFoundExceptions(Exception ex, WebRequest request) {
//    HttpErrorInfo errorDetails = new HttpErrorInfo(NOT_FOUND, request.getDescription(false), ex.getMessage());
//    return new ResponseEntity<>(errorDetails, NOT_FOUND);
//  }
//
//  @ExceptionHandler(InvalidInputException.class)
//  public ResponseEntity<HttpErrorInfo> handleInvalidInputException(Exception ex, WebRequest request) {
//    HttpErrorInfo errorDetails = new HttpErrorInfo(BAD_REQUEST, request.getDescription(false), ex.getMessage());
//    return new ResponseEntity<>(errorDetails, BAD_REQUEST);
//  }
//
//}
