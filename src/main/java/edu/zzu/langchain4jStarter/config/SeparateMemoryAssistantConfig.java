package edu.zzu.langchain4jStarter.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import edu.zzu.langchain4jStarter.provider.AdvancedChatMemoryProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeparateMemoryAssistantConfig {
    @Autowired
    private AdvancedChatMemoryProvider advancedChatMemoryProvider;

    @Bean
    public ChatMemoryProvider chatMemoryProvider(){
        return advancedChatMemoryProvider;
    }
}
