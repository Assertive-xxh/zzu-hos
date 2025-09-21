package edu.zzu.langchain4jStarter.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.index.Index;
import org.springframework.stereotype.Component;

@Component
public class MongoIndexInitializer implements CommandLineRunner {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public void run(String... args) {
        // 为memoryId字段创建索引，提升查询性能
        mongoTemplate.indexOps("chat_messages")
            .ensureIndex(new Index().on("memoryId", org.springframework.data.domain.Sort.Direction.ASC).unique());
    }
}