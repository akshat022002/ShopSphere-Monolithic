package com.akshat.shopsphere.ai.controller;

import com.akshat.shopsphere.ai.service.AiService;
import com.akshat.shopsphere.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@RequestMapping("/api/ai")
@RestController
public class AiController {

    private final AiService aiService;

    @GetMapping("/ask")
    public ResponseEntity<ApiResponse<String>> ask(@RequestParam String question){

        String response = aiService.ask(question);

        ApiResponse<String> apiResponse = new ApiResponse<>(true,
                "Ai response generated successfully",
                response,
                LocalDateTime.now());

        return ResponseEntity.ok(apiResponse);
    }
}
