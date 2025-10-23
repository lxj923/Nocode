package com.lxj.nocode.core.saver;

import com.lxj.nocode.ai.model.HtmlCodeResult;
import com.lxj.nocode.ai.model.MultiFileCodeResult;
import com.lxj.nocode.exception.BusinessException;
import com.lxj.nocode.exception.ErrorCode;
import com.lxj.nocode.model.enums.CodeGenTypeEnum;

public class MutiFileCodeFileSaver extends CodeFileSaverTemplate<MultiFileCodeResult>{

    @Override
    protected CodeGenTypeEnum getBizType() {
        return CodeGenTypeEnum.MULTI_FILE;
    }

    @Override
    protected void saveFile(String uniqueDirPath, MultiFileCodeResult result) {
        writeSingleFile(uniqueDirPath, "index.html", result.getHtmlCode());
        writeSingleFile(uniqueDirPath, "index.js", result.getJsCode());
        writeSingleFile(uniqueDirPath, "index.css", result.getCssCode());
    }

    @Override
    protected void validateInput(MultiFileCodeResult result) {
        super.validateInput(result);
        if (result.getHtmlCode() == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "htmlCode不能为空");
        }
    }
}
