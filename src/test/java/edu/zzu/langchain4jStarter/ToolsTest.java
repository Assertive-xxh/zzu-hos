package edu.zzu.langchain4jStarter;

import edu.zzu.langchain4jStarter.assistant.SeparateMemoryAssistant;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = zzuHosAss.class)
public class ToolsTest {

    @Resource
    private SeparateMemoryAssistant separateMemoryAssistant;
    @Test
    public void toolsTest(){
        String chat = separateMemoryAssistant.chat(2, "计算123131413的开方是多少");
        System.out.println(chat);
    }
}
