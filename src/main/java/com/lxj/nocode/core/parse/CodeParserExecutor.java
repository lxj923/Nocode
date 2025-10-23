package com.lxj.nocode.core.parse;

import com.lxj.nocode.exception.BusinessException;
import com.lxj.nocode.exception.ErrorCode;
import com.lxj.nocode.model.enums.CodeGenTypeEnum;


/**
 * 代码解析执行器
 * 根据代码生成类型执行对应逻辑
 */
public class CodeParserExecutor {

    private static final HtmlCodeParser htmlCodeParser = new HtmlCodeParser();

    private static final MultiFileCodeParser multiFileCodeParser = new MultiFileCodeParser();


    public static Object executeParse(String code, CodeGenTypeEnum codeGenTypeEnum) {
        return switch (codeGenTypeEnum) {
            case CodeGenTypeEnum.HTML -> htmlCodeParser.parseCode(code);
            case CodeGenTypeEnum.MULTI_FILE -> multiFileCodeParser.parseCode(code);
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的代码类型");
        };
    }
}
