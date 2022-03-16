package com.ps2site.util;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Mailer {

    public static boolean sendMail(String mailTitle, String mailContent, String email){
        log.info("发送邮件：{},{},{}",email, mailTitle, mailContent);
        return true;
    }
}
