package com.lxj.nocode.core.saver;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.lxj.nocode.exception.BusinessException;
import com.lxj.nocode.exception.ErrorCode;
import com.lxj.nocode.model.enums.CodeGenTypeEnum;

import java.io.File;

/**
 * 代码文件保存模板
 * @param <T>
 */
public abstract class CodeFileSaverTemplate<T> {

    // 文件保存根目录
    private static final String BASE_DIR = System.getProperty("user.dir") + "/tmp/code_output";

    /**
     * 模板方法：保存代码的标准流程
     * @param result 代码结果对象
     * @return 保存后的目录
     */
    public final File saveCode(T result) {
        //验证输入
        validateInput(result);
        //构建唯一目录
        String uniqueDirPath = buildUniqueDirPath(getBizType().getValue());
        //保存文件
        saveFile(uniqueDirPath, result);
        //返回
        return new File(uniqueDirPath);
    }

    //由子类继承
    protected abstract CodeGenTypeEnum getBizType();

    //由子类继承
    protected abstract void saveFile(String uniqueDirPath, T result);

    /**
     * 验证输入参数，子类可以覆盖
     * @param result 代码对象
     */
    protected void validateInput(T result) {
        if (result == null) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "输入参数为空");
        }
    }

    /**
     * 写入单个文件
     */
    protected final void writeSingleFile(String filePath, String filename, String content) {
        if (StrUtil.isNotBlank(content)){
            String Path = filePath + File.separator + filename;
            FileUtil.writeString(content, Path, "UTF-8");
        }
    }

    /**
     * 构建唯一目录路径：tmp/code_output/bizType_雪花ID
     */
     protected final String buildUniqueDirPath(String bizType) {
        String DirPath = StrUtil.format("{}_{}", bizType, IdUtil.getSnowflakeNextIdStr());
        String uniqueDirPath = BASE_DIR + File.separator + DirPath;
        FileUtil.mkdir(uniqueDirPath);
        return uniqueDirPath;
    }
}
