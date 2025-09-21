package edu.zzu.langchain4jStarter.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import io.swagger.v3.oas.annotations.Parameter;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "qwenChatModel",
        chatMemoryProvider = "chatMemoryProvider",
        tools = "calculatorTools"
)
public interface SeparateMemoryAssistant {
//    @SystemMessage("你是东北人，用正宗东北话对话。今天是:{{current_date}}")
    @SystemMessage(fromResource = "promptTest.txt")
    String chat(@MemoryId int memoryId, @UserMessage String message);

    @SystemMessage(fromResource = "promptTest2.txt")
    String chat2(@MemoryId int memoryId, @UserMessage String message,
                 @V("username") String username,
                 @V("age") int age
                 );
}
