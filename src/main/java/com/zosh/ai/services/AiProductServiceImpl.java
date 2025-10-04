package com.zosh.ai.services;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import okhttp3.*;

import java.io.IOException;

@Service
public class AiProductServiceImpl implements AiProductService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    @Override
    public String simpleChat(String prompt) {
        OkHttpClient client = new OkHttpClient();

        JSONObject message = new JSONObject()
                .put("role", "user")
                .put("content", prompt);

        JSONObject requestBody = new JSONObject()
                .put("model", "gpt-3.5-turbo")
                .put("messages", new JSONArray().put(message))
                .put("temperature", 0.7);

        Request request = new Request.Builder()
                .url(OPENAI_API_URL)
                .header("Authorization", "Bearer " + openaiApiKey)
                .header("Content-Type", "application/json")
                .post(RequestBody.create(requestBody.toString(), MediaType.parse("application/json")))
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                String responseBody = response.body().string();
                JSONObject json = new JSONObject(responseBody);
                JSONArray choices = json.getJSONArray("choices");
                return choices.getJSONObject(0).getJSONObject("message").getString("content").trim();
            } else {
                return "Error: " + response.code() + " - " + response.message();
            }
        } catch (IOException e) {
            return "Failed to call OpenAI: " + e.getMessage();
        }
    }
}
