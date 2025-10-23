package com.lxj.nocode.core;

import com.lxj.nocode.ai.AiCodeGeneratorService;
import com.lxj.nocode.ai.model.HtmlCodeResult;
import com.lxj.nocode.ai.model.MultiFileCodeResult;
import com.lxj.nocode.core.parse.CodeParserExecutor;
import com.lxj.nocode.core.saver.CodeFileSaverExecutor;
import com.lxj.nocode.exception.BusinessException;
import com.lxj.nocode.exception.ErrorCode;
import com.lxj.nocode.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.io.File;

/**
 * 门面类 AI 生成代码，组合生成和创建文件
 */
@Service
@Slf4j
public class AiCodeGenerator {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    /**
     * 根据用户输入生成代码并保存到文件中
     *
     * @param UserPrompt      用户输入
     * @param codeGenTypeEnum 代码生成类型
     * @return 生成的代码文件
     */
    public File generateAndSaveCode(String UserPrompt, CodeGenTypeEnum codeGenTypeEnum) {
        if (UserPrompt == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户输入不能为空");
        }

        return switch (codeGenTypeEnum) {
            case CodeGenTypeEnum.HTML -> {
                HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(UserPrompt);
                yield CodeFileSaverExecutor.CodeFileSaver(htmlCodeResult, CodeGenTypeEnum.HTML);
            }
            case CodeGenTypeEnum.MULTI_FILE -> {
                MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateSeveralFileCode(UserPrompt);
                yield CodeFileSaverExecutor.CodeFileSaver(multiFileCodeResult, CodeGenTypeEnum.MULTI_FILE);
            }
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的代码生成类型");
        };
    }

    /**
     * 根据用户输入生成代码并保存到文件中 (流式)
     *
     * @param UserPrompt      用户输入
     * @param codeGenTypeEnum 代码生成类型
     * @return 生成的代码文件
     */
    public Flux<String> generateAndSaveCodeStream(String UserPrompt, CodeGenTypeEnum codeGenTypeEnum) {
        if (UserPrompt == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户输入不能为空");
        }

        return switch (codeGenTypeEnum) {
            case CodeGenTypeEnum.HTML -> {
                Flux<String> stringFlux = aiCodeGeneratorService.generateHtmlCodeStream(UserPrompt);
                yield generateAndSaveCodeStream(stringFlux, codeGenTypeEnum);
            }
            case CodeGenTypeEnum.MULTI_FILE -> {
                Flux<String> stringFlux = aiCodeGeneratorService.generateSeveralFileCodeStream(UserPrompt);
                yield generateAndSaveCodeStream(stringFlux, codeGenTypeEnum);
            }
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的代码生成类型");
        };
    }


    /**
     * 通用流式代码处理
     * @param stringFlux 代码流
     * @param codeGenTypeEnum 代码生成类型
     * @return 流式响应
     */
    private Flux<String> generateAndSaveCodeStream(Flux<String> stringFlux, CodeGenTypeEnum codeGenTypeEnum) {
        //保存拼接的结果
        StringBuilder ResultContent = new StringBuilder();

        return stringFlux.doOnNext(
                chunk -> {
                    //实时拼接字符串
                    ResultContent.append(chunk);
                }).doOnComplete(
                () -> {
                    try {
                        //执行器解析字符串保存文件
                        Object CodeResult = CodeParserExecutor.executeParse(ResultContent.toString(), codeGenTypeEnum);
                        File file = CodeFileSaverExecutor.CodeFileSaver(CodeResult, codeGenTypeEnum);

                        log.info("文件保存成功: {}", file.getAbsolutePath());
                    }catch (Exception e){
                        log.error("文件保存失败: {}", e.getMessage());
                    }
                });
    }
}
