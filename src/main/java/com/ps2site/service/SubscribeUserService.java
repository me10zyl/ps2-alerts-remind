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
import com.yilnz.qqbotlib.exception.NotQQFriendException;
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
import java.util.function.Function;

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

    public List<SubscribeUser> getUsers(String qq) {
        LambdaQueryWrapper<SubscribeUser> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(qq != null, SubscribeUser::getQq, qq);
        return subscribeUserDao.selectList(queryWrapper);
    }

    public void addUser(SubscribeUser user){
        user.setAlertStartTime(null);
        if(user.getServer() == null){
            throw new BizException("请填写服务器");
        }
        if(user.getQq() == null && user.getEmail() == null){
            throw new BizException("请填写邮箱地址");
        }
        if(user.getEmail() != null) {
            Long count = subscribeUserDao.selectCount(new LambdaQueryWrapper<SubscribeUser>()
                    .eq(SubscribeUser::getEmail, user.getEmail())
                    .eq(SubscribeUser::getServer, user.getServer())
            );
            if (count > 0) {
                throw new BizException("该邮箱已经订阅过了，不要重复订阅");
            }
        }
        if(user.getQq() != null) {
            Long count = subscribeUserDao.selectCount(new LambdaQueryWrapper<SubscribeUser>()
                    .eq(SubscribeUser::getQq, user.getQq())
                    .eq(SubscribeUser::getServer, user.getServer())
            );
            if (count > 0) {
                throw new BizException("该QQ已经订阅过了，不要重复订阅");
            }
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
            if(user.getQq() == null && user.getEmail() == null){
                return;
            }
            String email = user.getEmail();
            String server = user.getServer();
            if(alertResult.getAlertStartTime().equals(user.getAlertStartTime())){
                log.info("{}-{}-{}已经发送过了，跳过", email, user.getQq(), server);
                return;
            }
            Map<String, Object> variableMap = new HashMap<>(jsonObject);
            variableMap.put("server", server);
            variableMap.put("alertStartTimeFormat", DateUtil.format(alertResult.getAlertStartTime(), "MM/dd HH:mm:ss"));
            variableMap.put("alertEndTimeFormat", DateUtil.format(alertResult.getAlertEndTime(), "MM/dd HH:mm:ss"));
            variableMap.put("durationFormat", alertResult.getDuration()/60 + "分钟");
            MailTemplateUtil mailTemplateUtil = new MailTemplateUtil(variableMap);
            boolean sendQQSuccess = false;
            boolean sendMailSuccess = false;
            if(user.getQq() != null){
                List<QQMessage> qqMessageList = new ArrayList<QQMessage>();
                qqMessageList.add(QQMessage.textMessage(mailTemplateUtil.getQQMessage()));
                if(qqBot == null){
                    log.error("QQ机器人未初始化成功！");
                    throw new BizException("QQ机器人未初始化成功");
                }
                try {
                    if (user.getIsQQGroup()) {
                        sendQQSuccess = qqBot.sendGroupMessage(user.getQq(), qqMessageList);
                        log.info("发送QQ群消息：" + user.getQq() + ", success=" + sendQQSuccess);
                    } else {
                        sendQQSuccess = qqBot.sendFriendMessage(user.getQq(), qqMessageList);
                        log.info("发送QQ消息：" + user.getQq() + ", success=" + sendQQSuccess);
                    }
                }catch (NotQQFriendException exception){
                    log.info(exception.getMessage() + ",自动删除订阅,qq=" + user.getQq() + ",email=" + user.getEmail() +",server=" + user.getServer());
                    delete(user.getEmail(), user.getQq(), user.getServer());
                    return;
                }
            }
            if(user.getEmail() != null) {
                sendMailSuccess =  Mailer.sendMail(mailTemplateUtil.getTitle(), mailTemplateUtil.getContent(), email);
                log.info("发送邮件：" + user.getEmail() + ", success=" +  sendMailSuccess);
            }
            if(sendQQSuccess || sendMailSuccess){
                user.setServer(null);;
                user.setEmail(null);
                user.setQq(null);
                user.setIsQQGroup(null);
                user.setAlertStartTime(alertResult.getAlertStartTime());
                subscribeUserDao.update(user, new LambdaUpdateWrapper<SubscribeUser>()
                        .eq(email != null, SubscribeUser::getEmail, email)
                        .eq(user.getQq() != null, SubscribeUser::getQq, user.getQq())
                        .eq(SubscribeUser::getServer, server)
                );
                log.info("更新数据库成功：email={},server={},qq={}", email, server, user.getQq());
            }
        });
    }

    public boolean delete(String email, String qq, String server) {
        int delete = subscribeUserDao.delete(new LambdaQueryWrapper<SubscribeUser>()
                .eq(email != null, SubscribeUser::getEmail, email)
                .eq(qq != null, SubscribeUser::getQq, qq)
                .eq(SubscribeUser::getServer, server));
        if(delete > 0 ){
            return true;
        }
        return false;
    }
}
