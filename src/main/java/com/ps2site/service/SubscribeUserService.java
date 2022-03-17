package com.ps2site.service;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ps2site.dao.SubscribeUserDao;
import com.ps2site.domain.AlertResult;
import com.ps2site.domain.SubscribeUser;
import com.ps2site.exception.BizException;
import com.ps2site.util.MailTemplateUtil;
import com.ps2site.util.Mailer;
import com.yilnz.qqbotlib.QQBot;
import com.yilnz.qqbotlib.entity.QQMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

@Service
@Slf4j
public class SubscribeUserService {
    @Autowired
    private SubscribeUserDao subscribeUserDao;
    @Autowired
    private QQBot qqBot;

    public List<SubscribeUser> getUsers() {
        return subscribeUserDao.selectList(new LambdaQueryWrapper<>());
    }

    public void addUser(SubscribeUser user){
        if(user.getEmail() == null || user.getServer() == null){
            throw new BizException("请填写邮件和服务器");
        }
        Long count = subscribeUserDao.selectCount(new LambdaQueryWrapper<SubscribeUser>()
                .eq(SubscribeUser::getEmail, user.getEmail())
                .eq(SubscribeUser::getServer, user.getServer())
        );
        if(count > 0){
            throw new BizException("该邮箱已经订阅过了，不要重复订阅");
        }
        subscribeUserDao.insert(user);
    }

    public static void main(String[] args) {
        String format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss Z", Locale.ENGLISH).format(new Date());
        System.out.println(format);
    }

    @Transactional
    public void deliveryAlertStartedEMailsAndQQ(String serverName, AlertResult alertResult) {
        JSONObject jsonObject = JSONObject.parseObject(JSONObject.toJSONString(alertResult));
        getUsers().stream().filter(u->u.getServer().equals(serverName)).forEach(user -> {
            String email = user.getEmail();
            String server = user.getServer();
            if(alertResult.getAlertStartTime().equals(user.getAlertStartTime())){
                log.info("{}-{}已经发送过了，跳过", email, server);
                return;
            }
            Map<String, Object> variableMap = new HashMap<>(jsonObject);
            variableMap.put("server", server);
            variableMap.put("alertStartTimeFormat", DateUtil.format(alertResult.getAlertStartTime(), "MM/dd HH:mm:ss"));
            variableMap.put("alertEndTimeFormat", DateUtil.format(alertResult.getAlertEndTime(), "MM/dd HH:mm:ss"));
            variableMap.put("durationFormat", alertResult.getDuration()/60 + "分钟");
            MailTemplateUtil mailTemplateUtil = new MailTemplateUtil(variableMap);
            boolean sendSuccess = Mailer.sendMail(mailTemplateUtil.getTitle(), mailTemplateUtil.getContent(), email);
            if(user.getQq() != null){
                List<QQMessage> qqMessageList = new ArrayList<QQMessage>();
                qqMessageList.add(QQMessage.textMessage(mailTemplateUtil.getQQMessage()));
                sendSuccess = qqBot.sendMessage(user.getQq(), qqMessageList);
            }
            if(sendSuccess){
                user.setServer(null);;
                user.setEmail(null);
                user.setQq(null);
                user.setAlertStartTime(alertResult.getAlertStartTime());
                subscribeUserDao.update(user, new LambdaUpdateWrapper<SubscribeUser>()
                        .eq(SubscribeUser::getEmail, email)
                        .eq(SubscribeUser::getServer, server)
                );
                log.info("发送成功：{},{}", email, server);
            }
        });
    }

    public boolean delete(String email, String server) {
        int delete = subscribeUserDao.delete(new LambdaQueryWrapper<SubscribeUser>()
                .eq(SubscribeUser::getEmail, email)
                .eq(SubscribeUser::getServer, server));
        if(delete > 0 ){
            return true;
        }
        return false;
    }
}
