package com.translator.webchat.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChatMessageRequestDto {
    private Long sessionId;
    private String sender;
    private String recipient;
    private String content;
    private String token;
}
