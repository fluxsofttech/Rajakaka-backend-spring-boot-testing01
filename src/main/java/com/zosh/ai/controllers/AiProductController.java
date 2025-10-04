package com.zosh.ai.controllers;

import com.zosh.ai.services.AiProductService;
import com.zosh.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
@RequiredArgsConstructor
public class AiProductController {

    private final AiProductService productService;

    @PostMapping("/chat/demo")
    public ResponseEntity<ApiResponse> generate(@RequestParam(
            value = "message",
            defaultValue = "Tell me a joke") String message) {

        try {
            // Call your service method to get response text from OpenAI
            String replyText = productService.simpleChat(message);

            ApiResponse apiResponse = new ApiResponse(replyText.trim(), true);
            return ResponseEntity.ok(apiResponse);

        } catch (Exception e) {
            ApiResponse error = new ApiResponse("❌ Error: " + e.getMessage(), false);
            return ResponseEntity.internalServerError().body(error);
        }
    }
}
