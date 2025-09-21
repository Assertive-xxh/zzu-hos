package edu.zzu.langchain4jStarter;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.AiServices;
import edu.zzu.langchain4jStarter.assistant.Assistant;
import edu.zzu.langchain4jStarter.assistant.MemoryAssistant;
import edu.zzu.langchain4jStarter.assistant.SeparateMemoryAssistant;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = zzuHosAss.class)
public class ChatMemoryTest {

    @Resource
    private QwenChatModel qwenChatModel;

    @Test
    public void testChatMemory() {

        MessageWindowChatMemory chatMemory = MessageWindowChatMemory.withMaxMessages(10);

        Assistant assistant = AiServices
                .builder(Assistant.class)
                .chatLanguageModel(qwenChatModel)
                .chatMemory(chatMemory)
                .build();

        String chat1 = assistant.chat("我是zzuHosAss");
        System.out.println(chat1);

        String chat2 = assistant.chat("我是谁？");
        System.out.println(chat2);
    }
    @Resource
    private MemoryAssistant memoryAssistant;
    @Test
    public void testMemoryAssistant() {
        String chat1 = memoryAssistant.chat("我是张三");
        System.out.println(chat1);
        String chat2 = memoryAssistant.chat("我是谁？");
        System.out.println(chat2);
    }

    @Resource
    private SeparateMemoryAssistant  separateMemoryAssistant;
    @Test
    public void testSeparateMemoryAssistant() {
        String c1 = separateMemoryAssistant.chat(1, "我是zzu");
        String c2 = separateMemoryAssistant.chat(1, "我是谁");
        String c3 = separateMemoryAssistant.chat(2, "我是谁");

        System.out.println(c1);
        System.out.println(c2);
        System.out.println(c3);
    }

}
