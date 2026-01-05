package com.example.aichat;

import ch.qos.logback.classic.LoggerContext;
import com.example.aichat.service.SimpleChatService;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Scanner;

@Configuration
public class SimpleChatConfig {

    @Bean
    public SimpleLoggerAdvisor simpleLoggerAdvisor() {
        return SimpleLoggerAdvisor.builder().build();
    }

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().maxMessages(10).build();
    }

    @Bean
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory chatMemory) {
        return MessageChatMemoryAdvisor.builder(chatMemory).build();
    }

    @ConditionalOnProperty(prefix = "spring.application", name = "cli", havingValue = "true")
    @Bean
    public CommandLineRunner cli(@Value("${spring.application.name}") String applicationName, SimpleChatService chatService, SimpleChatService simpleChatService) {
        return args -> {
            LoggerContext context = (LoggerContext) LoggerFactory.getILoggerFactory();
            context.getLogger("ROOT").detachAppender("CONSOLE");

            System.out.println("\n" + applicationName + "CLI Chat Bot");

            try (Scanner scanner = new Scanner(System.in)) {
                while (true) {
                    System.out.print("\nUser: ");
                    String userMessage = scanner.nextLine();
                    chatService.stream(Prompt.builder().content(userMessage).build(), "cli")
                            .doFirst(() -> System.out.print("\nAssistant: "))
                            .doOnNext(System.out::print)
                            .doOnComplete(System.out::println)
                            .blockLast();
                }
            }
        };
    }
}
