package com.ps2site.schedule;

import com.ps2site.domain.AlertResult;
import com.ps2site.service.AlertSpider;
import com.ps2site.service.SubscribeUserService;
import com.ps2site.util.ServerConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class AlertScheduled {

    @Autowired
    private AlertSpider alertSpider;

    @Autowired
    private SubscribeUserService subscribeUserService;

    @Scheduled(fixedDelay = 300000 ,initialDelay = 0)//5min
    public void schedule5Min(){
        for (String serverName : ServerConstants.getServerNames()) {

            AlertResult alertStarted = alertSpider.isAlertStarted(serverName);
            log.info("检查{}警报：{}", serverName, alertStarted.isStarted() ? "已开始, 开始时间：" + alertStarted.getPubDate() : "未开始");
            if(alertStarted.isStarted()){
                log.info("开始给{}的订阅用户发送邮件/QQ", serverName);
                subscribeUserService.deliveryAlertStartedEMailsAndQQ(serverName, alertStarted);
            }
        }
    }
}
