package com.lxj.nocode.ai;

import dev.langchain4j.model.chat.ChatModel;
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

    // 注册代码生成器
    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService() {
        return AiServices.create(AiCodeGeneratorService.class, chatModel);
    }
}
