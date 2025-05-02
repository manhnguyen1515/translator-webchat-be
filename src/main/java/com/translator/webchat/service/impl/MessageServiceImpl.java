package com.translator.webchat.service.impl;

import com.translator.webchat.consts.SupportedLanguage;
import com.translator.webchat.dto.request.ChatMessageRequestDto;
import com.translator.webchat.dto.response.ChatMessageResponseDto;
import com.translator.webchat.dto.response.MessageLanguage;
import com.translator.webchat.entities.Message;
import com.translator.webchat.entities.Session;
import com.translator.webchat.entities.User;
import com.translator.webchat.repositories.MessageRepository;
import com.translator.webchat.repositories.SessionRepository;
import com.translator.webchat.repositories.UserRepository;
import com.translator.webchat.service.MessageService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.translator.webchat.service.TranslatorApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final SessionRepository sessionRepository;
    private final TranslatorApiService translatorApiService;

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

        try {
            String originLanguage = chatMessage.getLanguage();
            // Loop Enum SupportedLanguage and filter the language not originLanguage
            List<String> toLanguages = Arrays.stream(SupportedLanguage.values()).filter(language -> !language.equalsName(originLanguage)).map(SupportedLanguage::toString).collect(Collectors.toList());

            MessageLanguage messageResponse = translatorApiService.fetchTranslatorText(chatMessage.getContent(), originLanguage, toLanguages);

            return messageRepository.save(Message.builder()
                    .user(sender.get())
                    .session(session.get())
                    .contentEn(messageResponse.getEnglish())
                    .contentJa(messageResponse.getJapanese())
                    .contentVi(messageResponse.getVietnamese())
                    .createdAt(LocalDateTime.now())
                    .build());
        } catch (IOException e) {
            return null;
        }

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
            String nestedJson = rootNode.path("translations")
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
                            .contentEn(message.getContentEn())
                            .contentVi(message.getContentVi())
                            .contentJa(message.getContentJa())
                            .updatedAt(message.getUpdatedAt())
                            .createdAt(message.getCreatedAt()).build();
                }
        ).collect(Collectors.toList());
    }
}
