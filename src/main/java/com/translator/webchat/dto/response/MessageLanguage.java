package com.translator.webchat.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class MessageLanguage {
    private String vietnamese;
    private String japanese;
    private String english;
}
