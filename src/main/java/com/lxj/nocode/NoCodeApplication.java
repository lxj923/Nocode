package com.lxj.nocode;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy(exposeProxy = true) //可以访问 Aop 实现的代理对象
@MapperScan("com.lxj.nocode.mapper")
public class NoCodeApplication {

    public static void main(String[] args) {
        SpringApplication.run(NoCodeApplication.class, args);
    }

}
