package edu.zzu.langchain4jStarter;

import dev.langchain4j.community.model.dashscope.QwenChatModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = zzuHosAss.class)
public class langchain4jTest {


    @Test
    public void testGPTDemo() {
        // This is a placeholder for the actual test logic.
        // You can implement your test cases here.
        OpenAiChatModel builder = OpenAiChatModel.builder()
                .baseUrl("http://langchain4j.dev/demo/openai/v1")
                .apiKey("Demo")
                .modelName("gpt-4o-mini")
                .build();
        String chat = builder.chat("你的版本是什么？");
        System.out.println(chat);
    }

    @Resource
    private OpenAiChatModel chatModel;

    @Test
    public void testSpringBoot() {
        String chat = chatModel.chat("你是谁？");
        System.out.println(chat);
    }

//    阿里百炼测试
    @Resource
    private QwenChatModel qwenChatModel;
    @Test
    public void testQwenChatModel() {
        String chat = qwenChatModel.chat("你是谁？");
    }
}
