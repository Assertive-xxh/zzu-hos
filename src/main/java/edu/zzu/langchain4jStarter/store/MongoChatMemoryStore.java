package edu.zzu.langchain4jStarter.store;

import dev.langchain4j.data.message.ChatMessage;
import dev.langchain4j.data.message.ChatMessageDeserializer;
import dev.langchain4j.data.message.ChatMessageSerializer;
import dev.langchain4j.store.memory.chat.ChatMemoryStore;
import edu.zzu.langchain4jStarter.bean.ChatMessages;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class MongoChatMemoryStore implements ChatMemoryStore {

    private static final String SHORT_TERM_MEMORY = "SHORT_TERM";
    private static final String LONG_TERM_MEMORY = "LONG_TERM";

    @Autowired
    private MongoTemplate mongoTemplate;

    // 兼容旧的 getMessages 方法，默认获取短期记忆
    @Override
    public List<ChatMessage> getMessages(Object memoryId) {
        return getMessages(memoryId, SHORT_TERM_MEMORY);
    }

    public List<ChatMessage> getMessages(Object memoryId, String memoryType) {
        Query query = new Query(Criteria.where("memoryId").is(memoryId).and("memoryType").is(memoryType));
        ChatMessages chatMessages = mongoTemplate.findOne(query, ChatMessages.class);

        if (chatMessages == null || chatMessages.getContent() == null) {
            return new ArrayList<>();
        }

        return ChatMessageDeserializer.messagesFromJson(chatMessages.getContent());
    }

    // 兼容旧的 updateMessages 方法，默认更新短期记忆
    @Override
    public void updateMessages(Object memoryId, List<ChatMessage> messages) {
        updateMessages(memoryId, messages, SHORT_TERM_MEMORY);
    }

    public void updateMessages(Object memoryId, List<ChatMessage> messages, String memoryType) {
        Query query = new Query(Criteria.where("memoryId").is(memoryId).and("memoryType").is(memoryType));
        Update update = new Update()
                .set("content", ChatMessageSerializer.messagesToJson(messages))
                .set("memoryType", memoryType); // 确保 memoryType 被设置

        mongoTemplate.upsert(query, update, ChatMessages.class);
    }

    // 兼容旧的 deleteMessages 方法，默认删除短期记忆
    @Override
    public void deleteMessages(Object memoryId) {
        deleteMessages(memoryId, SHORT_TERM_MEMORY);
    }

    public void deleteMessages(Object memoryId, String memoryType) {
        Query query = new Query(Criteria.where("memoryId").is(memoryId).and("memoryType").is(memoryType));
        mongoTemplate.remove(query, ChatMessages.class);
    }

    /**
     * 将短期记忆合并到长期记忆中
     * @param memoryId 记忆ID
     */
    public void consolidateShortTermToLongTerm(Object memoryId) {
        List<ChatMessage> shortTermMessages = getMessages(memoryId, SHORT_TERM_MEMORY);
        if (shortTermMessages.isEmpty()) {
            return;
        }

        List<ChatMessage> longTermMessages = getMessages(memoryId, LONG_TERM_MEMORY);
        
        // 这里可以加入更复杂的摘要或筛选逻辑
        // 例如，只保留用户和AI的最后几轮对话，或者对对话进行摘要
        
        longTermMessages.addAll(shortTermMessages);

        updateMessages(memoryId, longTermMessages, LONG_TERM_MEMORY);
        deleteMessages(memoryId, SHORT_TERM_MEMORY);
    }
}