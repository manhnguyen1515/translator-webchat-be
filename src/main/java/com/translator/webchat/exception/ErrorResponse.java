package com.translator.webchat.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ErrorResponse {
    private int status;
    private String message;
    private Object data;
    private ErrorResponseInfo additionalInfo;

}