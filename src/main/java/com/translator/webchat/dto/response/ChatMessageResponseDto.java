package com.translator.webchat.dto.response;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Builder
public class ChatMessageResponseDto implements Serializable {
    private static final long serialVersionUID = 1L;

    private String id;
    private String sender;
    private String recipient;
    private String contentEn;
    private String contentVi;
    private String contentJa;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
