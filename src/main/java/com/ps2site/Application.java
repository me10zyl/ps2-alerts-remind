package com.ps2site;

import com.baomidou.mybatisplus.autoconfigure.MybatisPlusAutoConfiguration;
import com.baomidou.mybatisplus.autoconfigure.MybatisPlusLanguageDriverAutoConfiguration;
import com.ps2site.test.Testcase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication(exclude = {MybatisPlusAutoConfiguration.class, MybatisPlusLanguageDriverAutoConfiguration.class})
@EnableScheduling
public class Application {

    @Autowired
    private Testcase testcase;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
