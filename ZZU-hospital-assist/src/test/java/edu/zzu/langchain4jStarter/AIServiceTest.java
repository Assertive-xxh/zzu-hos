package edu.zzu.langchain4jStarter;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.service.AiServices;
import edu.zzu.langchain4jStarter.assistant.Assistant;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = zzuHosAss.class)
public class AIServiceTest {
    @Resource
    private QwenChatModel qwenChatModel;

    @Test
    public void testQwenChatModel() {
        Assistant assistant = AiServices.create(Assistant.class, qwenChatModel);
        String response = assistant.chat("你是谁？");
        System.out.println(response);
    }

    @Resource
    private Assistant assistant;
    @Test
    public void testAssistant() {
        String response = assistant.chat("你是谁？");
        System.out.println(response);
    }


}
