package com.example.aichat.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
public class SimpleChatService {

    private final ChatClient chatClient;

    public SimpleChatService(ChatClient.Builder chatClientBuilder, Advisor[] advisors) {
        this.chatClient = chatClientBuilder.defaultAdvisors(advisors).build();
    }

    public Flux<String> stream(Prompt prompt, String conversationId) {
        return buildChatClientRequestSpec(prompt, conversationId).stream().content();
    }

    private ChatClient.ChatClientRequestSpec buildChatClientRequestSpec(Prompt prompt, String conversationId) {
        return chatClient.prompt(prompt).advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId));
    }
}
