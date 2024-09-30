package com.translator.webchat.controller;

import com.translator.webchat.dto.request.ChatMessageRequestDto;
import com.translator.webchat.dto.response.ChatMessageResponseDto;
import com.translator.webchat.entities.Message;
import com.translator.webchat.service.impl.MessageServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.util.ObjectUtils;

import java.util.Random;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageServiceImpl messageService;

    @MessageMapping("/message")
    public void processMessage(@Payload ChatMessageRequestDto chatMessage) {
        Message message = messageService.saveChatMessage(chatMessage);

        ChatMessageResponseDto chatMessageResponse = ChatMessageResponseDto.builder()
                .id(ObjectUtils.isEmpty(message) ? String.valueOf(new Random().nextInt()) : message.getId().toString())
                .sender(chatMessage.getSender())
                .recipient(chatMessage.getRecipient())
                .content(chatMessage.getContent())
                .contentVi(ObjectUtils.isEmpty(message) ? chatMessage.getContent() : message.getContentVi())
                .contentKo(ObjectUtils.isEmpty(message) ? chatMessage.getContent() : message.getContentKo())
                .createdAt(ObjectUtils.isEmpty(message) ? null : message.getCreatedAt())
                .updatedAt(ObjectUtils.isEmpty(message) ? null : message.getUpdatedAt())
                .build();
        this.messagingTemplate.convertAndSend(
                "/topic/user/" + chatMessage.getRecipient(),
                chatMessageResponse
        );

        if (!ObjectUtils.isEmpty(message) && !ObjectUtils.isEmpty(message.getSession())) {
            messageService.updateMessageToRedis(chatMessageResponse, message.getSession());
        }
    }
}
