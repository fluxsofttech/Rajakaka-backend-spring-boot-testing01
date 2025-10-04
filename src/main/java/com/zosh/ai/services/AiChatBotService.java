package com.zosh.ai.services;

import com.zosh.exception.ProductException;
import com.zosh.response.ApiResponse;

/**
 * Service interface for interacting with OpenAI chatbot.
 */
public interface AiChatBotService {

    /**
     * Calls OpenAI's Chat Completion API to generate a response for the given prompt.
     *
     * @param prompt     The user input or question.
     * @param productId  (Optional) Product ID related to the context.
     * @param userId     (Optional) ID of the user making the request.
     * @return ApiResponse containing the AI-generated message.
     * @throws ProductException if the AI call fails or input is invalid.
     */
    ApiResponse aiChatBot(String prompt, Long productId, Long userId) throws ProductException;
}
