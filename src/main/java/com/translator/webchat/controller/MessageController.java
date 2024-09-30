package com.translator.webchat.controller;

import com.translator.webchat.dto.request.CreateSessionDTO;
import com.translator.webchat.dto.response.ChatMessageResponseDto;
import com.translator.webchat.dto.response.MessagesResponseDto;
import com.translator.webchat.dto.response.ResponseData;
import com.translator.webchat.service.MessageService;
import com.translator.webchat.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/api/messages")
@Validated
@Slf4j
@Tag(name = "Message", description = "The Message API")
public class MessageController {
    private final MessageService messageService;
    private final UserService userService;

    @Operation(summary = "Get message", description = "Get the message", responses = {
            @ApiResponse(responseCode = "200", description = "Message found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "message found", summary = "return message",
                                    value = "{\"status\": 200, \"message\": \"Message found\", \"data\": [{\"id\": \"id\", \"username\": \"username\", \"content\": \"content\"}]}"
                            ))),
            @ApiResponse(responseCode = "404", description = "Message not found",
                    content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                            examples = @ExampleObject(name = "message not found", summary = "message not found",
                                    value = "{status: 404, message: \"Message not found\", data: \"1\"}"
                            )))
    })
    @GetMapping("/")
    public ResponseEntity<ResponseData<MessagesResponseDto>> getMessage(@RequestParam String username, @RequestParam String friend)
            throws NoSuchAlgorithmException, InvalidKeySpecException {
        String sessionId = userService.createSession(new CreateSessionDTO(username, friend));
        List<ChatMessageResponseDto> messages = messageService.getFirstFifteenMessages(sessionId);

        ResponseData<MessagesResponseDto> responseData = new ResponseData<>(
                HttpStatus.OK.value(),
                "Message found",
                MessagesResponseDto.builder().sessionId(sessionId).messages(messages).build());
        return ResponseEntity.ok(responseData);
    }
}
