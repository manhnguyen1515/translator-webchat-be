package com.translator.webchat.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.util.Date;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(Exception e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        ErrorResponseInfo errorResponseInfo = new ErrorResponseInfo();

        errorResponseInfo.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponseInfo.setPath(request.getDescription(false).replace("uri=", ""));

        String message = e.getMessage();
        if (e instanceof MethodArgumentNotValidException) {
            int start = message.lastIndexOf("[");
            int end = message.lastIndexOf("]");
            message = message.substring(start + 1, end - 1);
            errorResponse.setMessage(message);
            errorResponseInfo.setError("Payload Invalid");
        } else if (e instanceof ConstraintViolationException) {
            message = message.substring(message.indexOf(" ") + 1);
            errorResponseInfo.setError("PathVariable Invalid");
        }
        errorResponse.setMessage(message);
        errorResponse.setAdditionalInfo(errorResponseInfo);

        return errorResponse;
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e, WebRequest request) {
        System.out.println("=======================> INTERNAL_SERVER_ERROR <=======================");
        ErrorResponse errorResponse = new ErrorResponse();
        ErrorResponseInfo errorResponseInfo = new ErrorResponseInfo();
        errorResponseInfo.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        errorResponseInfo.setPath(request.getDescription(false).replace("uri=", ""));
        errorResponseInfo.setError("Internal Server Error");
        if (e instanceof MethodArgumentTypeMismatchException) {
                    errorResponse.setMessage("Failed to convert value of type 'java.lang.String' to required type");
        }
        errorResponse.setAdditionalInfo(errorResponseInfo);
        return errorResponse;
    }

    @ExceptionHandler({ResponseStatusException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleResponseStatusException(ResponseStatusException e, WebRequest request) {
        ErrorResponse errorResponse = new ErrorResponse();
        ErrorResponseInfo errorResponseInfo = new ErrorResponseInfo();
        errorResponseInfo.setTimestamp(new Date());
        errorResponse.setStatus(HttpStatus.BAD_REQUEST.value());
        errorResponseInfo.setPath(request.getDescription(false).replace("uri=", ""));

        String message = e.getMessage();
        int start = message.indexOf("\"");
        int end = message.lastIndexOf("\"");
        if (start >= 0 && end > start) {
            message = message.substring(start + 1, end);
        }
        errorResponse.setMessage(message);
        errorResponseInfo.setError("ResponseStatusException occurred");
        errorResponse.setAdditionalInfo(errorResponseInfo);
        return errorResponse;
    }

}