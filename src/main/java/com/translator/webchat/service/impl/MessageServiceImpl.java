package com.translator.webchat.service.impl;

import com.translator.webchat.dto.request.ChatMessageRequestDto;
import com.translator.webchat.dto.response.ChatMessageResponseDto;
import com.translator.webchat.entities.Message;
import com.translator.webchat.entities.Session;
import com.translator.webchat.entities.User;
import com.translator.webchat.repositories.MessageRepository;
import com.translator.webchat.repositories.SessionRepository;
import com.translator.webchat.repositories.UserRepository;
import com.translator.webchat.service.MessageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final RedisServiceImpl redisService;
    private final GeminiServiceImpl geminiService;

    /**
     * @{inheritDoc}
     */
    @Override
    public Message saveChatMessage(ChatMessageRequestDto chatMessage) {
        Optional<User> sender = userRepository.findByUsername(chatMessage.getSender());
        Optional<Session> session = sessionRepository.findById(chatMessage.getSessionId());
        if (sender.isEmpty() || session.isEmpty()) {
            return null;
        }

        String apiResponse = geminiService.callApi(chatMessage.getContent());
        JsonNode nestedNode = this.parseMessageFromJson(apiResponse);
        String contentVi = nestedNode.path("contentVi").asText();
        String contentKo = nestedNode.path("contentKo").asText();

        return messageRepository.save(Message.builder()
                .user(sender.get())
                .session(session.get())
                .content(chatMessage.getContent())
                .contentKo(contentKo)
                .contentVi(contentVi)
                .createdAt(LocalDateTime.now())
                .build());

    }

    @Override
    public void updateMessageToRedis(ChatMessageResponseDto chatMessageResponse, Session session) {
        Message lastMessage = this.getLastElementOfLastFifteenMessage(session);

        if (!ObjectUtils.isEmpty(lastMessage)) {
            redisService.delete(lastMessage.getId().toString(), session.getId().toString());
        }
        redisService.saveMessage(chatMessageResponse, session.getId().toString());
    }

    private Message getLastElementOfLastFifteenMessage(Session session) {
        Pageable pageable = PageRequest.of(0, 15);
        List<Message> lastFifteenMessages = messageRepository.findBySessionOrderByIdDesc(session, pageable);
        if (lastFifteenMessages.isEmpty()) {
            return null;
        }
        return lastFifteenMessages.get(lastFifteenMessages.size() - 1);
    }

    /**
     * Parse message from response of gemini api response
     * @param json JSON string
     * @return parsed message
     */
    private JsonNode parseMessageFromJson(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            // Parse the outer JSON to get the nested JSON string
            JsonNode rootNode = mapper.readTree(json);
            String nestedJson = rootNode.path("candidates")
                    .path(0)
                    .path("content")
                    .path("parts")
                    .path(0)
                    .path("text")
                    .asText();

            return mapper.readTree(nestedJson);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<ChatMessageResponseDto> getFirstFifteenMessages(String sessionId) {
        // Attempt to retrieve messages from Redis
        List<ChatMessageResponseDto> messages = redisService.findFirstFifteenMessages(sessionId);

        if (ObjectUtils.isEmpty(messages)) {
            // If Redis doesn't have the messages, query the database
            Pageable pageable = PageRequest.of(0, 15, Sort.by("id").descending());
            List<Message> messageEntities = messageRepository.findBySessionId(Long.parseLong(sessionId), pageable);

            // Convert entities to DTOs (assuming a method exists for this conversion)
            return messageEntities.stream().map(message -> {
                User sender = message.getUser();
                Optional<User> recipientOpt = message.getSession().getUsers().stream().filter(user -> !user.getId().equals(sender.getId())).findFirst();
                return ChatMessageResponseDto.builder()
                        .id(message.getId().toString())
                        .sender(sender.getUsername())
                        .recipient(recipientOpt.map(User::getUsername).orElse(""))
                        .content(message.getContent())
                        .contentVi(message.getContentVi())
                        .contentKo(message.getContentKo())
                        .updatedAt(message.getUpdatedAt())
                        .createdAt(message.getCreatedAt()).build();
                    }
            ).collect(Collectors.toList());
        }
        return messages;
    }
}
