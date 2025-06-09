package edu.zzu.langchain4jStarter.config;

import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import edu.zzu.langchain4jStarter.store.MongoChatMemoryStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SeparateMemoryAssistantConfig {
    @Autowired
    private MongoChatMemoryStore mongoChatMemoryStore;

    @Bean
    public ChatMemoryProvider chatMemoryProvider(){
        return memoryId -> MessageWindowChatMemory
                .builder()
                .id(memoryId)
                .chatMemoryStore(mongoChatMemoryStore)
                .maxMessages(10).build();
    }
}
