package com.lxj.nocode.core.parse;

/**
 * 代码解析器策略接口
 * @param <T>
 */
public interface CodeParser<T> {
    /**
     * 解析代码
     * @param code 代码
     * @return 解析结果
     */
    T parseCode(String code);
}
