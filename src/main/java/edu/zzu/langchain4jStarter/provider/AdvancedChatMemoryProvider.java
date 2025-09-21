package edu.zzu.langchain4jStarter.provider;

import dev.langchain4j.memory.ChatMemory;
import dev.langchain4j.memory.chat.ChatMemoryProvider;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import edu.zzu.langchain4jStarter.store.MongoChatMemoryStore;
import edu.zzu.langchain4jStarter.memory.CompositeChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 提供具有长短期记忆能力的ChatMemory实例
 */
@Component
public class AdvancedChatMemoryProvider implements ChatMemoryProvider {

    private final MongoChatMemoryStore memoryStore;

    @Autowired
    public AdvancedChatMemoryProvider(MongoChatMemoryStore memoryStore) {
        this.memoryStore = memoryStore;
    }

    @Override
    public ChatMemory get(Object memoryId) {
        // 短期记忆：保留最近20条消息
        ChatMemory shortTermMemory = MessageWindowChatMemory.builder()
                .maxMessages(20)
                .id(memoryId)
                .chatMemoryStore(new DelegatingChatMemoryStore(memoryId, "SHORT_TERM", memoryStore))
                .build();

        // 长期记忆：保留大量历史记录
        ChatMemory longTermMemory = MessageWindowChatMemory.builder()
                .maxMessages(1000)
                .id(memoryId)
                .chatMemoryStore(new DelegatingChatMemoryStore(memoryId, "LONG_TERM", memoryStore))
                .build();
        
        // 返回一个组合了长短期记忆的实例
        return new CompositeChatMemory(shortTermMemory, longTermMemory, memoryId);
    }

    /**
     * 内部委托类，将特定类型的记忆请求路由到增强的MongoChatMemoryStore
     */
    private static class DelegatingChatMemoryStore implements ChatMemoryStore {
        private final Object memoryId;
        private final String memoryType;
        private final MongoChatMemoryStore delegate;

        public DelegatingChatMemoryStore(Object memoryId, String memoryType, MongoChatMemoryStore delegate) {
            this.memoryId = memoryId;
            this.memoryType = memoryType;
            this.delegate = delegate;
        }

        @Override
        public java.util.List<dev.langchain4j.data.message.ChatMessage> getMessages(Object ignored) {
            return delegate.getMessages(memoryId, memoryType);
        }

        @Override
        public void updateMessages(Object ignored, java.util.List<dev.langchain4j.data.message.ChatMessage> messages) {
            delegate.updateMessages(memoryId, messages, memoryType);
        }

        @Override
        public void deleteMessages(Object ignored) {
            delegate.deleteMessages(memoryId, memoryType);
        }
    }
}
