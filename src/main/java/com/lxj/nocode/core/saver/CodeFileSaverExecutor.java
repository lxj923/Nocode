package com.lxj.nocode.core.saver;

import com.lxj.nocode.ai.model.HtmlCodeResult;
import com.lxj.nocode.ai.model.MultiFileCodeResult;
import com.lxj.nocode.exception.BusinessException;
import com.lxj.nocode.exception.ErrorCode;
import com.lxj.nocode.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * 代码文件保存执行器
 * 根据类型执行对应保存逻辑
 */
public class CodeFileSaverExecutor {

    private static final HtmlCodeFileSaver htmlCodeFileSaver = new HtmlCodeFileSaver();

    private static final MutiFileCodeFileSaver mutiFileCodeFileSaver = new MutiFileCodeFileSaver();

    /**
     * 根据类型执行保存逻辑
     * @param result 代码结果对象
     * @param codeGenTypeEnum 代码生成类型
     * @return 保存后的目录
     */
    public static File CodeFileSaver(Object result, CodeGenTypeEnum codeGenTypeEnum) {
        return switch (codeGenTypeEnum) {
            case CodeGenTypeEnum.HTML -> htmlCodeFileSaver.saveCode((HtmlCodeResult) result);
            case CodeGenTypeEnum.MULTI_FILE -> mutiFileCodeFileSaver.saveCode((MultiFileCodeResult) result);
            default -> throw new BusinessException(ErrorCode.PARAMS_ERROR, "不支持的代码生成类型");
        };
    }
}
