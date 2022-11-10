package com.kezaihui.faq;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @Author: lerry_li
 * @CreateDate: 2022/03/31
 * @Description
 */
@SpringBootApplication(scanBasePackages = "com.kezaihui")
@MapperScan({
        "com.kezaihui.faq.dao"
})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

}
