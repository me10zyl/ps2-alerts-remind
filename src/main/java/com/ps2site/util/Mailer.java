package com.ps2site.util;


import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

import java.util.Properties;

@Slf4j
@Component
public class Mailer {

    private static MailProperites mailProperites;

    @Autowired
    public void setMailProperites(MailProperites mailProperites) {
        Mailer.mailProperites = mailProperites;
    }

    public static boolean sendMail(String mailTitle, String mailContent, String email) {
        log.info("发送邮件：{},{},{}", email, mailTitle, mailContent);

        Properties properties = new Properties();
        properties.put("mail.transport.protocol", "smtp");// 连接协议
        properties.put("mail.smtp.host", mailProperites.getStmpServer());// 主机名
        properties.put("mail.smtp.port", mailProperites.getStmpPort());// 端口号
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.ssl.enable", "true");// 设置是否使用ssl安全连接 ---一般都使用
        properties.put("mail.debug", "false");// 设置是否显示debug信息 true 会在控制台显示相关信息
        Session session = Session.getInstance(properties);
        Message message = new MimeMessage(session);
        // 设置发件人邮箱地址
        try {
            message.setFrom(new InternetAddress(mailProperites.getUser()));
            // 设置收件人邮箱地址
            InternetAddress[] tos = new InternetAddress[]{new InternetAddress(email)};
            message.setRecipients(Message.RecipientType.TO, tos);
            //message.setRecipient(Message.RecipientType.TO, new InternetAddress("xxx@qq.com"));//一个收件人
            // 设置邮件标题
            message.setSubject(encodeSubject(mailTitle));
            // 设置邮件内容
            message.setContent(mailContent, false ? "text/html;charset=utf-8" : "text/plain;charset=utf-8");
            // 得到邮差对象
            Transport transport = session.getTransport();
            // 连接自己的邮箱账户
            transport.connect(mailProperites.getUser(), mailProperites.getPassword());// 密码为QQ邮箱开通的stmp服务后得到的客户端授权码
            // 发送邮件
            transport.sendMessage(message, message.getAllRecipients());
            transport.close();
        } catch (MessagingException e) {
            log.error("发送邮件失败", e);
            return false;
        }
        return true;
    }

    private static String encodeSubject(String subject) {
//		byte[] base64 = org.apache.commons.codec.binary.Base64.encodeBase64(subject.getBytes());
        try {
            return MimeUtility.encodeText(subject, MimeUtility.mimeCharset("utf-8"), null);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
//		return String.format("=?UTF-8?B?%s?=", new String(base64));
    }
}
