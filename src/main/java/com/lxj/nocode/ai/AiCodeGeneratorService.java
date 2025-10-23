package com.lxj.nocode.ai;

import com.lxj.nocode.ai.model.HtmlCodeResult;
import com.lxj.nocode.ai.model.MultiFileCodeResult;
import dev.langchain4j.service.SystemMessage;
import reactor.core.publisher.Flux;

/**
 * 定义 AI 服务的“功能”，声明要生成什么类型的代码。
 */
public interface AiCodeGeneratorService {

    /**
     * 生成单文件代码
     * @param userPrompt 用户提示词
     * @return AI生成的代码
     */
    @SystemMessage(fromResource = "prompt/Nocode_HtmlCode_SystemPrompt.txt")
    HtmlCodeResult generateHtmlCode(String userPrompt);

    /**
     * 生成多文件代码
     * @param userPrompt 用户提示词
     * @return AI生成的代码
     */
    @SystemMessage(fromResource = "prompt/Nocode_SeveralCode_SystemPrompt.txt")
    MultiFileCodeResult generateSeveralFileCode(String userPrompt);


    /**
     * 生成单文件代码 (流式)
     * @param userPrompt 用户提示词
     * @return AI生成的代码
     */
    @SystemMessage(fromResource = "prompt/Nocode_HtmlCode_SystemPrompt.txt")
    Flux<String> generateHtmlCodeStream(String userPrompt);

    /**
     * 生成多文件代码 (流式)
     * @param userPrompt 用户提示词
     * @return AI生成的代码
     */
    @SystemMessage(fromResource = "prompt/Nocode_SeveralCode_SystemPrompt.txt")
    Flux<String> generateSeveralFileCodeStream(String userPrompt);
}
