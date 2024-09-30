package com.translator.webchat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseGetUser {
    private String username;
    private String nickname;
}
