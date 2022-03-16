package com.ps2site.util;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

@Component
@Data
@PropertySource({"classpath:smtp.properties"})
public class MailProperites {
    @Value("${mail.user}")
    private String user;
    @Value("${mail.password}")
    private String password;
    @Value("${mail.smtp.server}")
    private String stmpServer;
    @Value("${mail.smtp.port}")
    private int stmpPort;
}
