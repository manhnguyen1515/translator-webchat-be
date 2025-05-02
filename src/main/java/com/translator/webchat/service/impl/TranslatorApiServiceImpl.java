package com.translator.webchat.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.translator.webchat.consts.SupportedLanguage;
import com.translator.webchat.dto.response.MessageLanguage;
import com.translator.webchat.service.TranslatorApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TranslatorApiServiceImpl implements TranslatorApiService {

    @Value("${app.ai.azure.api-key}")
    private String azureKey;

    private static String location = "southeastasia";
    // Instantiates the OkHttpClient.
    OkHttpClient client = new OkHttpClient();

    /**
     * @{inheritDoc}
     */
    @Override
    public MessageLanguage fetchTranslatorText(String text, String originLanguage, List<String> toLanguages) throws IOException {
        MediaType mediaType = MediaType.parse("application/json");
        RequestBody body = RequestBody.create(mediaType,
                "[{\"Text\": \"" + text + "\"}]");
        String translatorUrl = "https://api.cognitive.microsofttranslator.com/translate?api-version=3.0&from="
                + originLanguage
                + "&to=" + String.join( "&to=", toLanguages);
        Request request = new Request.Builder()
                .url(translatorUrl)
                .post(body)
                .addHeader("Ocp-Apim-Subscription-Key", azureKey)
                // location required if you're using a multi-service or regional (not global) resource.
                .addHeader("Ocp-Apim-Subscription-Region", location)
                .addHeader("Content-type", "application/json")
                .build();
        Response response = client.newCall(request).execute();
        MessageLanguage messageFromRes = this.parseMessageFromJson(response.body().string());
        return this.setMessageLanguage(SupportedLanguage.fromName(originLanguage), text, messageFromRes);
    }


    /**
     * Parse message from response of gemini api response
     * @param json JSON string
     * @return parsed message
     */
    private MessageLanguage parseMessageFromJson(String json) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode translationsNode = root.get(0).get("translations");

            MessageLanguage messageLanguage = MessageLanguage.builder().build();
            for (JsonNode translation : translationsNode) {
                String text = translation.get("text").asText();
                String to = translation.get("to").asText();
                SupportedLanguage language = SupportedLanguage.fromName(to);
                messageLanguage = this.setMessageLanguage(language, text, messageLanguage);
            }
            return messageLanguage;

        } catch (Exception e) {
            e.printStackTrace();
            return MessageLanguage.builder().build();
        }
    }

    private MessageLanguage setMessageLanguage(SupportedLanguage supportedLanguage, String text, MessageLanguage messageLanguage) {
        MessageLanguage newLanguage = messageLanguage.toBuilder().build();
        switch (supportedLanguage) {
            case English -> newLanguage.setEnglish(text);
            case Vietnam -> newLanguage.setVietnamese(text);
            case Japanese -> newLanguage.setJapanese(text);
        }
        return newLanguage;
    }
}
