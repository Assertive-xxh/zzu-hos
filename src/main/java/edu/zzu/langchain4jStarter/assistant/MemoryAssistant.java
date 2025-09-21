package edu.zzu.langchain4jStarter.assistant;

import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "qwenChatModel",
        chatMemory = "chatMemory"
)
public interface MemoryAssistant {
    @UserMessage("你是小郑，一位乐于助人的智能医疗助手。你的主要职责是提供医疗健康方面的咨询和初步建议。{{message}}")
    String chat(@V("message") String message);
}
