package com.zosh.ai.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
public class ProductDetailsBotServiceImpl implements ProductDetailsBotService {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    @Override
    public String productDetailsChatBot(String prompt) {
        // Construct the request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("model", "gpt-3.5-turbo");

        JSONArray messages = new JSONArray();
        messages.put(new JSONObject()
                .put("role", "system")
                .put("content", "You are a helpful assistant specialized in product details."));

        messages.put(new JSONObject()
                .put("role", "user")
                .put("content", prompt));

        requestBody.put("messages", messages);
        requestBody.put("temperature", 0.7);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(openAiApiKey);

        HttpEntity<String> httpEntity = new HttpEntity<>(requestBody.toString(), headers);
        RestTemplate restTemplate = new RestTemplate();

        try {
            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, httpEntity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                JSONObject responseBody = new JSONObject(response.getBody());
                JSONArray choices = responseBody.getJSONArray("choices");
                if (choices.length() > 0) {
                    return choices.getJSONObject(0).getJSONObject("message").getString("content");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Something went wrong while communicating with OpenAI.";
        }

        return "No response from OpenAI.";
    }
}
