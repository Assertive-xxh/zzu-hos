package edu.zzu.langchain4jStarter;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;

@SpringBootTest(classes = zzuHosAss.class)
public class MongoCrudTest {
    @Autowired
    private MongoTemplate mongoTemplate;

    /**
     * 插入文档测试
     */
    @Test
    public void testInsert() {
        // 创建测试数据
//        mongoTemplate.insert(new ChatMessages(1L, "聊天记录"));

    }
}
