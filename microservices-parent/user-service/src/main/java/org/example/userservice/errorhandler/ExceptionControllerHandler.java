package org.example.userservice.errorhandler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
@RestControllerAdvice
public class ExceptionControllerHandler {
    private final Logger LOGGER = LoggerFactory.getLogger(ExceptionControllerHandler.class);

    @ExceptionHandler(UserException.class)
    public ResponseEntity<HttpErrorResponse> userException(UserException exception)
    {
        LOGGER.error(exception.getMessage());
        return createHttpResponse(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    private ResponseEntity<HttpErrorResponse> createHttpResponse(HttpStatus httpStatus, String message){
        HttpErrorResponse response = new HttpErrorResponse();
        response.setTimeStamp(LocalDateTime.now());
        response.setHttpStatusCode(httpStatus.value());
        response.setReason(httpStatus.getReasonPhrase().toUpperCase());
        response.setMessage(message);
        return new ResponseEntity<>(response, httpStatus);
    }

}
