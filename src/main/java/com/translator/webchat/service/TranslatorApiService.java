package com.translator.webchat.service;

import com.translator.webchat.dto.response.MessageLanguage;

import java.io.IOException;
import java.util.List;

public interface TranslatorApiService {

    /**
     * This function performs a POST request.
     * @param text Text to translate
     * @return
     */
    MessageLanguage fetchTranslatorText(String text, String originLanguage, List<String> toLanguages) throws IOException;
}
