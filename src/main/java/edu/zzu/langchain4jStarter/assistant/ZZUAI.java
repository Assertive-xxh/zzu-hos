package edu.zzu.langchain4jStarter.assistant;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

@AiService(wiringMode = AiServiceWiringMode.EXPLICIT,
//        chatModel = "qwenChatModel",
        streamingChatModel = "qwenStreamingChatModel",
        chatMemoryProvider = "chatMemoryProviderZZU",
        tools = "appointmentTools",
        contentRetriever = "contentRetrieverZZUPinecone"
)
public interface ZZUAI {
    @SystemMessage(fromResource = "ZZUAIPrompt.txt")
    Flux<String> chat(@MemoryId Long memoryId, @UserMessage String userMessage);
}
