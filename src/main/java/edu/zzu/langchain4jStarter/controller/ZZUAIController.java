package edu.zzu.langchain4jStarter.controller;

import edu.zzu.langchain4jStarter.assistant.ZZUAI;
import edu.zzu.langchain4jStarter.bean.ChatForm;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@Tag(name = "小郑助手")
@RestController
@RequestMapping("/xiaozheng")
public class ZZUAIController {

    @Resource
    private ZZUAI zZUAI;

    @Operation(summary = "对话")
    @PostMapping(value = "/chat", produces = "text/stream;charset=utf-8")
    public Flux<String> chat(@RequestBody ChatForm chatForm){
        return zZUAI.chat(chatForm.getMemoryId(), chatForm.getMessage());
    }
}
