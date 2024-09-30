package com.translator.webchat.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
public class MessagesResponseDto implements Serializable {
    private String sessionId;
    private List<ChatMessageResponseDto> messages;
}
