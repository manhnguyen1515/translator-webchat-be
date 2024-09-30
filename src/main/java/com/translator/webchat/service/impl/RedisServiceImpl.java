package com.translator.webchat.service.impl;

import com.translator.webchat.dto.response.ChatMessageResponseDto;
import com.translator.webchat.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class RedisServiceImpl implements RedisService {

    private static final String SESSION_HASH_KEY = "session_";

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    private HashOperations<Object, String, ChatMessageResponseDto> hashOperations;

    public RedisServiceImpl(RedisTemplate<Object, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public void saveMessage(ChatMessageResponseDto message, String sessionId) {
        hashOperations.put(SESSION_HASH_KEY + sessionId, message.getId(), message);
    }

    @Override
    public Map<String, ChatMessageResponseDto> findMessagesInSession(String sessionId) {
        return hashOperations.entries(SESSION_HASH_KEY + sessionId);
    }

    @Override
    public ChatMessageResponseDto findById(String id, String sessionId) {
        return hashOperations.get(SESSION_HASH_KEY + sessionId, id);
    }

    @Override
    public void update(ChatMessageResponseDto message, String sessionId) {
        this.saveMessage(message, sessionId);
    }

    @Override
    public void delete(String id, String sessionId) {
        hashOperations.delete(SESSION_HASH_KEY + sessionId, id);
    }

    @Override
    public List<ChatMessageResponseDto> findFirstFifteenMessages(String sessionId) {
        Map<String, ChatMessageResponseDto> messagesMap = hashOperations.entries(SESSION_HASH_KEY + sessionId);

        // Assuming ChatMessageResponseDto has a timestamp or similar field for sorting
        // This example does not handle sorting as it's unclear how messages are to be sorted
        // You might need to add a comparator to sort by timestamp or another relevant field
        return messagesMap.values().stream()
                .sorted(Comparator.comparing(ChatMessageResponseDto::getCreatedAt).reversed()) // Assuming getCreatedAt exists and you want the latest messages
                .limit(15)
                .collect(Collectors.toList());
    }
}
