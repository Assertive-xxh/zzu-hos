package edu.zzu.langchain4jStarter.memory;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.memory.ChatMemory;
import lombok.AllArgsConstructor;

import java.util.List;

/**
 * 组合长短期记忆的ChatMemory实现
 */
@AllArgsConstructor
public class CompositeChatMemory implements ChatMemory {

    private final ChatMemory shortTermMemory;
    private final ChatMemory longTermMemory;
    private final Object memoryId;

    @Override
    public Object id() {
        return memoryId;
    }

    @Override
    public void add(ChatMessage message) {
        shortTermMemory.add(message);
        longTermMemory.add(message); // 也可以在这里做摘要后添加
    }

    @Override
    public List<ChatMessage> messages() {

        List<ChatMessage> messages = shortTermMemory.messages();
        if (messages.isEmpty()) {
            return longTermMemory.messages();
        }
        return messages;
    }

    @Override
    public void clear() {
        shortTermMemory.clear();
        // 通常不清除长期记忆，除非用户要求
        // longTermMemory.clear();
    }
}
