package com.lxj.nocode.core;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.lxj.nocode.ai.model.HtmlCodeResult;
import com.lxj.nocode.ai.model.MultiFileCodeResult;
import com.lxj.nocode.model.enums.CodeGenTypeEnum;

import java.io.File;

public class CodeFileSaver {
    // 文件保存根目录
    private static final String BASE_DIR = "temp/code_output";

    /**
     * 保存 HtmlCodeResult
     */
    public static File saveHtmlCodeResult(HtmlCodeResult htmlCodeResult) {
        String filePath = buildUniqueDirPath(CodeGenTypeEnum.HTML.getValue());
        writeSingleFile(filePath, "index.html", htmlCodeResult.getHtmlCode());
        return new File(filePath);
    }


    /**
     * 保存 MultiFileCodeResult
     */
    public static File saveMultiFileCodeResult(MultiFileCodeResult multiFileCodeResult) {
        String filePath = buildUniqueDirPath(CodeGenTypeEnum.MULTI_FILE.getValue());
        writeSingleFile(filePath, "index.html", multiFileCodeResult.getHtmlCode());
        writeSingleFile(filePath, "index.js", multiFileCodeResult.getJsCode());
        writeSingleFile(filePath, "index.css", multiFileCodeResult.getCssCode());
        return new File(filePath);
    }

    /**
     * 构建唯一目录路径：tmp/code_output/bizType_雪花ID
     */
    private static String buildUniqueDirPath(String bizType) {
        String DirPath = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextIdStr());
        String uniqueDirPath = BASE_DIR + File.separator + DirPath;
        FileUtil.mkdir(uniqueDirPath);
        return uniqueDirPath;
    }


    /**
     * 写入单个文件
     */
    private static void writeSingleFile(String filePath, String filename, String content) {
        String Path = filePath + File.separator + filename;
        FileUtil.writeString(content, Path, "UTF-8");
    }
}
