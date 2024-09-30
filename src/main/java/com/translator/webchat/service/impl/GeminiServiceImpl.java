package com.translator.webchat.service.impl;

import com.translator.webchat.service.GeminiService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@RequiredArgsConstructor
public class GeminiServiceImpl implements GeminiService {

    private final RestTemplate restTemplate;

    @Value("${app.ai.gemini.api-key}")
    private String geminiKey;

    private final String API_URL_TEMPLATE = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent";
    private final String systemPromptTemplate = "You are a language expert in the field of translation. You are knowledgeable and well-versed in the rules for translating into Vietnamese and Korean. You are familiar with abbreviations and teencode in both Vietnamese and Korean. Emoji usually appear after special characters symbol, so you do not need to translate the emoji. I will provide you with a message, and you must identify which language it is. Then, you will translate it into both Vietnamese and Korean. You will return the translation to me in JSON format where the \"contentVi\" attribute will contain the Vietnamese translation and the \"contentKo\" attribute will contain the Korean translation. For example, when the message is \"Xin Chào\" you will return the following format: {\n" +
            "    \"contentVi\": \"Xin chào\",\n" +
            "    \"contentKo\": \"안녕하세요\"\n" +
            "}\n";

    /**
     * @{inheritDoc}
     */
    @Override
    public String callApi(String message) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("x-goog-api-key", geminiKey);

        ObjectMapper objectMapper = new ObjectMapper();

        // system prompt
        ObjectNode promptNode = objectMapper.createObjectNode();
        promptNode.put("role", "model");
        promptNode.set("parts", objectMapper.createArrayNode().add(objectMapper.createObjectNode().put("text", systemPromptTemplate)));

        // user vietnamese example prompt
        ObjectNode userViPromptNode = objectMapper.createObjectNode();
        userViPromptNode.put("role", "user");
        userViPromptNode.set("parts", objectMapper.createArrayNode().add(objectMapper.createObjectNode().put("text", "Cảm ơn vì sự chăm chỉ của bạn :)")));

        // Assistant vietnamese prompt
        ObjectNode assistantViNode = objectMapper.createObjectNode();
        assistantViNode.put("role", "model");
        assistantViNode.set("parts", objectMapper.createArrayNode().add(objectMapper.createObjectNode().put("text", "{\n" +
                "    \"contentVi\": \"Cảm ơn vì sự chăm chỉ của bạn :)\",\n" +
                "    \"contentKo\": \"노고에 감사드립니다 :)\"\n" +
                "}\n")));

        // user korean example prompt
        ObjectNode userKoPromptNode = objectMapper.createObjectNode();
        userKoPromptNode.put("role", "user");
        userKoPromptNode.set("parts", objectMapper.createArrayNode().add(objectMapper.createObjectNode().put("text", "정말 감사합니다 :bow")));

        // Assistant korean prompt
        ObjectNode assistantKoNode = objectMapper.createObjectNode();
        assistantKoNode.put("role", "model");
        assistantKoNode.set("parts", objectMapper.createArrayNode().add(objectMapper.createObjectNode().put("text", "{\n" +
                "    \"contentVi\": \"Cảm ơn rất nhiều :bow\",\n" +
                "    \"contentKo\": \"정말 감사합니다 :bow\"\n" +
                "}\n")));

        // user english example prompt
        ObjectNode userEnPromptNode = objectMapper.createObjectNode();
        userEnPromptNode.put("role", "user");
        userEnPromptNode.set("parts", objectMapper.createArrayNode().add(objectMapper.createObjectNode().put("text", "Thanks for your good work. >3")));

        // Assistant english prompt
        ObjectNode assistantEnNode = objectMapper.createObjectNode();
        assistantEnNode.put("role", "model");
        assistantEnNode.set("parts", objectMapper.createArrayNode().add(objectMapper.createObjectNode().put("text", "{\n" +
                "    \"contentVi\": \"Cảm ơn vì sự chăm chỉ của bạn. >3\",\n" +
                "    \"contentKo\": \"좋은 일을 해주셔서 감사합니다. >3\"\n" +
                "}\n")));

        // User messages prompt
        ObjectNode userMessageNode = objectMapper.createObjectNode();
        userMessageNode.put("role", "user");
        userMessageNode.set("parts", objectMapper.createArrayNode().add(objectMapper.createObjectNode().put("text", message)));

        ObjectNode requestBodyNode = objectMapper.createObjectNode();
        ArrayNode contentsNode = objectMapper.createArrayNode();
        requestBodyNode.set("contents", contentsNode
                .add(promptNode)
                .add(userViPromptNode)
                .add(assistantViNode)
                .add(userKoPromptNode)
                .add(assistantKoNode)
                .add(userEnPromptNode)
                .add(assistantEnNode)
                .add(userMessageNode));

        String requestBody;
        try {
            requestBody = objectMapper.writeValueAsString(requestBodyNode);
        } catch (Exception e) {
            throw new RuntimeException("Failed to construct JSON request body", e);
        }

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        ResponseEntity<String> response = restTemplate.exchange(API_URL_TEMPLATE, HttpMethod.POST, request, String.class);

        return response.getBody();
    }
}
