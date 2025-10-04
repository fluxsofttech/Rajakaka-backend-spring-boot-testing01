package com.zosh.ai.services;

import com.zosh.exception.ProductException;
import com.zosh.mapper.OrderMapper;
import com.zosh.mapper.ProductMapper;
import com.zosh.model.*;
import com.zosh.repository.*;
import com.zosh.response.ApiResponse;
import com.zosh.response.FunctionResponse;
import lombok.RequiredArgsConstructor;
import okhttp3.*;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AiChatBotServiceImpl implements AiChatBotService {

    @Value("${openai.api.key}")
    private String openaiApiKey;

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;

    @Override
    public ApiResponse aiChatBot(String prompt, Long productId, Long userId) throws ProductException {
        OkHttpClient client = new OkHttpClient();

        JSONObject messageJson = new JSONObject();
        JSONArray messages = new JSONArray();
        messages.put(new JSONObject().put("role", "user").put("content", prompt));

        messageJson.put("model", "gpt-3.5-turbo");
        messageJson.put("messages", messages);

        RequestBody body = RequestBody.create(
                messageJson.toString(),
                MediaType.parse("application/json")
        );

        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .post(body)
                .addHeader("Authorization", "Bearer " + openaiApiKey)
                .addHeader("Content-Type", "application/json")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("Unexpected response from OpenAI: " + response);
            }

            String responseBody = response.body().string();
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray choices = jsonResponse.getJSONArray("choices");
            String content = choices.getJSONObject(0).getJSONObject("message").getString("content");

            ApiResponse apiResponse = new ApiResponse();
            apiResponse.setMessage(content);
            return apiResponse;
        } catch (IOException e) {
            throw new ProductException("OpenAI API request failed: " + e.getMessage());
        }
    }
}
