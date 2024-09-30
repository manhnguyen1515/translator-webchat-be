package com.translator.webchat.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.UUID;

@Data
@AllArgsConstructor
public class UserSignInResponse {
    private UUID userId;
    private String username;
    private String email;
    private String nickname;
    private String regionCountry;
}
