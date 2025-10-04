package com.zosh.ai.controllers;

import com.zosh.ai.services.AiChatBotService;
import com.zosh.model.User;
import com.zosh.request.Prompt;
import com.zosh.response.ApiResponse;
import com.zosh.service.UserService;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/ai/chat")
public class AiChatBotController {

    private final AiChatBotService aiChatBotService;
    private final UserService userService;
    @GetMapping("/test")
    public ResponseEntity<ApiResponse> testOpenAiConnection() {
        try {
            String testPrompt = "Say hello from Java Spring Boot";
            ApiResponse response = aiChatBotService.aiChatBot(testPrompt, null, null);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.failure("❌ OpenAI test failed: " + e.getMessage()));
        }
    }


    @PostMapping
    public ResponseEntity<ApiResponse> generate(
            @RequestBody Prompt prompt,
            @RequestParam(required = false) Long productId,
            @RequestHeader(name = "Authorization", required = false) String jwt
    ) {
        try {
            // Compose message to be passed to AI
            String message = prompt.getPrompt();
            if (productId != null) {
                message = "The product ID is " + productId + ", " + message;
            }

            // Extract user from JWT if present
            User user = null;
            Long userId = null;

            if (jwt != null && jwt.startsWith("Bearer ")) {
                jwt = jwt.substring(7);
                user = userService.findUserProfileByJwt(jwt);
                if (user != null) {
                    userId = user.getId();
                }
            }

            // Call service
            ApiResponse apiResponse = aiChatBotService.aiChatBot(message, productId, userId);
            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            e.printStackTrace(); // Optional: use a logger
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.failure("❌ Failed to generate AI response: " + e.getMessage()));
        }
    }
}