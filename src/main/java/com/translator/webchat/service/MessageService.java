package com.translator.webchat.service;

import com.translator.webchat.dto.request.ChatMessageRequestDto;
import com.translator.webchat.dto.response.ChatMessageResponseDto;
import com.translator.webchat.entities.Message;
import com.translator.webchat.entities.Session;

import java.util.List;

public interface MessageService {
    /**
     * Save chat message
     * @param chatMessage ChatMessageRequestDto
     * @return Message entity
     */
    Message saveChatMessage(ChatMessageRequestDto chatMessage);

    /**
     * Service for get first 15 messages
     * @param sessionId
     * @return
     */
    List<ChatMessageResponseDto> getFirstFifteenMessages(String sessionId);
}
