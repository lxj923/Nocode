package com.lxj.nocode.core;

import com.lxj.nocode.ai.AiCodeGeneratorService;
import com.lxj.nocode.ai.model.HtmlCodeResult;
import com.lxj.nocode.ai.model.MultiFileCodeResult;
import com.lxj.nocode.exception.BusinessException;
import com.lxj.nocode.exception.ErrorCode;
import com.lxj.nocode.model.enums.CodeGenTypeEnum;
import jakarta.annotation.Resource;

import java.io.File;

/**
 * 门面类 AI 生成代码，组合生成和创建文件
 */
public class AiCodeGenerator {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    /**
     * 根据用户输入生成代码并保存到文件中
     * @param UserPrompt 用户输入
     * @param codeGenTypeEnum 代码生成类型
     * @return 生成的代码文件
     */
    public File generateAndSaveCode(String UserPrompt, CodeGenTypeEnum codeGenTypeEnum) {
        if (UserPrompt == null){
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "用户输入不能为空");
        }

        return switch (codeGenTypeEnum) {
            case CodeGenTypeEnum.HTML -> generateAndSaveHtmlCode(UserPrompt);
            case CodeGenTypeEnum.MULTI_FILE -> generateAndSaveSeveralCode(UserPrompt);
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的代码生成类型");
        };
    }

    /**
     * 生成并保存多个文件代码
     * @param userPrompt 用户输入
     * @return 生成的代码文件
     */
    private File generateAndSaveSeveralCode(String userPrompt) {
        MultiFileCodeResult multiFileCodeResult = aiCodeGeneratorService.generateSeveralFileCode(userPrompt);
        return CodeFileSaver.saveMultiFileCodeResult(multiFileCodeResult);
    }

    /**
     * 生成并保存 HTML 代码
     * @param userPrompt 用户输入
     * @return 生成的代码文件
     */
    private File generateAndSaveHtmlCode(String userPrompt) {
        HtmlCodeResult htmlCodeResult = aiCodeGeneratorService.generateHtmlCode(userPrompt);
        return CodeFileSaver.saveHtmlCodeResult(htmlCodeResult);
    }

}
