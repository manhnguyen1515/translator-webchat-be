package com.translator.webchat.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper=false)
public class ResourceNotFoundException extends RuntimeException{

    private int status;
    private String message;
    private String data;

    public ResourceNotFoundException(int status, String message, String data) {
        this.status = status;
        this.message = message;
        this.data = data;
    }

}
