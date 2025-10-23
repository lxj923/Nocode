package com.lxj.nocode.ai;

import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 负责实例化AiCodeGeneratorService接口的实现（通过 LangChain4j 的 AiServices 动态代理机制）。
 */
@Configuration
public class AiCodeGeneratorServiceFactory {

    // 大模型
    @Resource
    private ChatModel chatModel;

    // 流式大模型
    @Resource
    private StreamingChatModel streamingChatModel;

    // 注册 AI 服务
    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return AiServices.builder(AiCodeGeneratorService.class)
                .chatModel(chatModel)
                .streamingChatModel(streamingChatModel)
                .build();
    }
}
