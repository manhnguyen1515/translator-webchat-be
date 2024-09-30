package com.translator.webchat.service;

import com.translator.webchat.dto.response.ChatMessageResponseDto;

import java.util.List;
import java.util.Map;

public interface RedisService {

    /**
     * Save chat message to Redis hash map
     * @param message ChatMessageResponseDto
     * @param sessionId sessionId
     */
    public void saveMessage(ChatMessageResponseDto message, String sessionId);

    /**
     * Find all chat messages in the session
     * @param sessionId sessionId
     * @return Map<String, ChatMessageResponseDto>
     */
    Map<String, ChatMessageResponseDto> findMessagesInSession(String sessionId);

    /**
     * Update chat message in Redis hash map
     * @param id ChatMessageResponseDto id
     * @param sessionId sessionId
     */
    ChatMessageResponseDto findById(String id, String sessionId);

    /**
     * Update chat message from Redis hash map
     * @param message ChatMessageResponseDto
     * @param sessionId sessionId
     */
    void update(ChatMessageResponseDto message, String sessionId);

    /**
     * Delete chat message from Redis hash map
     * @param id ChatMessageResponseDto id
     * @param sessionId sessionId
     */
    void delete(String id, String sessionId);

    /**
     * Find first 15 chat messages in the session
     * @param sessionId sessionId
     * @return List<ChatMessageResponseDto>
     */
    List<ChatMessageResponseDto> findFirstFifteenMessages(String sessionId);
}
