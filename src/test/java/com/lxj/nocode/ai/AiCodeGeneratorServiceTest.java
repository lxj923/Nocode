package com.lxj.nocode.ai;

import com.lxj.nocode.ai.model.HtmlCodeResult;
import com.lxj.nocode.ai.model.MultiFileCodeResult;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class AiCodeGeneratorServiceTest {

    @Resource
    private AiCodeGeneratorService aiCodeGeneratorService;

    @Test
    void generateHtmlCode() {
        HtmlCodeResult s = aiCodeGeneratorService.generateHtmlCode("给我做一个登录界面，只要 20行代码");
        System.out.println(s);
    }

    @Test
    void generateSeveralFileCode() {
        MultiFileCodeResult s = aiCodeGeneratorService.generateSeveralFileCode("给我做一个登录界面，只要 20行代码");
        System.out.println(s);
    }
}