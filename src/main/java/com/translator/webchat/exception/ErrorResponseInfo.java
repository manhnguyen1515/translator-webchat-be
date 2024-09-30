package com.translator.webchat.exception;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
public class ErrorResponseInfo {
    private String error;
    private Date timestamp;
    private String path;
}
