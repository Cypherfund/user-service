package com.cypherfund.campaign.user.exceptions;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.NOT_FOUND;

/**
 *
 * @author ngaielizabeth
 */
@Slf4j
@RestControllerAdvice
public class GlobalControllerExceptionHandler extends ResponseEntityExceptionHandler {
  @ExceptionHandler(Exception.class)
  public final ResponseEntity<HttpErrorInfo> handleAllExceptions(Exception ex, WebRequest request)  {
    HttpErrorInfo errorDetails = new HttpErrorInfo(INTERNAL_SERVER_ERROR, request.getDescription(false), ex.getMessage());
    return new ResponseEntity<>(errorDetails, INTERNAL_SERVER_ERROR);

  }
  @Override
  protected ResponseEntity<Object> handleExceptionInternal(Exception ex, @Nullable Object body, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
    if (ex instanceof MethodArgumentNotValidException) {
      BindingResult bindingResult = ((MethodArgumentNotValidException)ex).getBindingResult();
      HttpErrorInfo errorDetails = new HttpErrorInfo(BAD_REQUEST, request.getDescription(false), ex.getMessage());
      errorDetails.setMessage("");
      String error = "Field %s has error %s\n";
      for (FieldError fieldError : bindingResult.getFieldErrors()) {
        errorDetails.setMessage(errorDetails.getMessage().concat(String.format(error, fieldError.getField(), fieldError.getDefaultMessage())));
      }
      return new ResponseEntity<>(errorDetails, status);
    }
    return super.handleExceptionInternal(ex, body, headers, status, request);
  }
  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<HttpErrorInfo> handleNotFoundExceptions(Exception ex, WebRequest request) {
    HttpErrorInfo errorDetails = new HttpErrorInfo(NOT_FOUND, request.getDescription(false), ex.getMessage());
    return new ResponseEntity<>(errorDetails, NOT_FOUND);
  }

  @ExceptionHandler(InvalidInputException.class)
  public ResponseEntity<HttpErrorInfo> handleInvalidInputException(Exception ex, WebRequest request) {
    HttpErrorInfo errorDetails = new HttpErrorInfo(BAD_REQUEST, request.getDescription(false), ex.getMessage());
    return new ResponseEntity<>(errorDetails, BAD_REQUEST);
  }

}
