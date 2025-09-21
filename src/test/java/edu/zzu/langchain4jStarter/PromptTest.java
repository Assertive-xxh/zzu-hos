package edu.zzu.langchain4jStarter;

import edu.zzu.langchain4jStarter.assistant.MemoryAssistant;
import edu.zzu.langchain4jStarter.assistant.SeparateMemoryAssistant;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = zzuHosAss.class)
public class PromptTest {

    @Resource
    private SeparateMemoryAssistant  separateMemoryAssistant;

    @Test
    public void PromptMessageTest() {
        String chat1 = separateMemoryAssistant.chat(3, "俺是胡图图");
        String chat2 = separateMemoryAssistant.chat(3, "你认识俺吗");
        System.out.println(chat1);
        System.out.println(chat2);
    }

    @Test
    public void PromptMessageTest2() {
        String chat1 = separateMemoryAssistant.chat(4, "你是谁");
        System.out.println(chat1);
    }


    @Resource
    private MemoryAssistant  memoryAssistant;
    @Test
    public void PromptMessageTest3() {
        String chat1 = memoryAssistant.chat("你是谁");
        System.out.println(chat1);
    }

    @Test
    public void testUserInfo() {
        //假设已经获取用户信息
        String username = "小王";
        int age = 20;
        String chat = separateMemoryAssistant.chat2(6, "你是谁，我是谁", username, age);
        System.out.println(chat);
    }
}
