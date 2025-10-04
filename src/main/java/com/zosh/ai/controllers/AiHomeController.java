package com.zosh.ai.controllers;

import com.zosh.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiHomeController {

    @Value("${openai.api.key}")
    private String openAiApiKey;

    private final RestTemplate restTemplate = new RestTemplate();

    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";

    /**
     * Simple GET endpoint to verify that AI controller is alive
     */
    @GetMapping
    public ResponseEntity<ApiResponse> aiHome() {
        ApiResponse response = new ApiResponse("✅ Welcome to AI world!", true);
        return ResponseEntity.ok(response);
    }

    /**
     * Optional: Check OpenAI health with a sample prompt (debug or admin only)
     */
    @GetMapping("/check")
    public ResponseEntity<ApiResponse> checkOpenAIHealth() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAiApiKey);

            // Basic test message
            Map<String, Object> requestBody = Map.of(
                    "model", "gpt-3.5-turbo",
                    "messages", List.of(Map.of("role", "user", "content", "Hello")),
                    "temperature", 0.5
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_API_URL, entity, Map.class);

            if (response.getStatusCode() == HttpStatus.OK) {
                return ResponseEntity.ok(new ApiResponse("✅ OpenAI is reachable and responding.", true));
            } else {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                        .body(new ApiResponse("❌ OpenAI did not respond as expected.", false));
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ApiResponse("❌ Error checking OpenAI: " + e.getMessage(), false));
        }
    }
}
