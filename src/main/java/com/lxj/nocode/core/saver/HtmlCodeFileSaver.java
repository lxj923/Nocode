package com.lxj.nocode.core.saver;

import com.lxj.nocode.ai.model.HtmlCodeResult;
import com.lxj.nocode.exception.BusinessException;
import com.lxj.nocode.exception.ErrorCode;
import com.lxj.nocode.model.enums.CodeGenTypeEnum;

import java.io.File;

public class HtmlCodeFileSaver extends CodeFileSaverTemplate<HtmlCodeResult> {

    @Override
    protected CodeGenTypeEnum getBizType() {
        return CodeGenTypeEnum.HTML;
    }

    @Override
    protected void saveFile(String uniqueDirPath, HtmlCodeResult result) {
        writeSingleFile(uniqueDirPath, "index.html", result.getHtmlCode());
    }

    @Override
    protected void validateInput(HtmlCodeResult result) {
        super.validateInput(result);
        if (result.getHtmlCode() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "htmlCode不能为空");
        }
    }
}
