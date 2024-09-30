package com.translator.webchat.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class TokenRefreshException extends RuntimeException{
    private static final long serialVersionUID = 1L;

    private int status;
    private String message;
    private String data;

    public TokenRefreshException(int status, String message, String data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }
}
