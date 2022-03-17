package com.ps2site.config;

import com.yilnz.qqbotlib.QQBot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("qqbot.properties")
public class QQBotConfig {

    @Value("${qqbot.verifyCode}")
    private String verifyCode;
    @Value("${qqbot.qqNumber}")
    private String qqNumber;

    @Bean
    public QQBot qqbot(){
        QQBot qqBot = null;
        try {
             qqBot = new QQBot(qqNumber, verifyCode);
        }catch (Exception e){
            e.printStackTrace();
        }
        return qqBot;
    }

}
