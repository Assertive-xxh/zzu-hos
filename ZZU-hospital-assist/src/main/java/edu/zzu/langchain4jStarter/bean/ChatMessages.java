package edu.zzu.langchain4jStarter.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * 聊天消息实体类
 * 对应MongoDB中的chat_messages集合
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("chat_messages")
public class ChatMessages {
    
    /**
     * 唯一标识，映射到MongoDB文档的_id字段
     */
    @Id
    private ObjectId messageId;

    /**
     * 聊天记录ID，用于隔离聊天记录
     */
    private String memoryId;
    
    /**
     * 存储当前聊天记录表的json字符串
     */
    private String content;
}