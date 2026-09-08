package com.akshat.shopsphere.ai.service;

import com.akshat.shopsphere.ai.tool.ProductAiTool;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;


@Service
public class AiServiceImpl implements AiService{

    public AiServiceImpl(ChatClient.Builder chatClientBuilder,ProductAiTool productAiTool){
        this.chatClient = chatClientBuilder.build();
        this.productAiTool=productAiTool;
    }

    private final ChatClient chatClient;
    private final ProductAiTool productAiTool;

    @Override
    public String ask(String question) {

    return chatClient
            .prompt()
            .system("""
                    You are ShopSphere AI Assistant.
                    
                    You can answer general shopping questions.
                    
                    When the user asks about products available in ShopSphere,
                    use the ShopSphere product search tool.
                    
                    Never invent ShopSphere products or prices.
                    """)
            .user(question)
            .tools(productAiTool)
            .call()
            .content();

    }
}
