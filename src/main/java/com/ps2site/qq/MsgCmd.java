package com.ps2site.qq;

import com.ps2site.config.QQBotConfig;
import com.ps2site.domain.SubscribeUser;
import com.ps2site.exception.BizException;
import com.ps2site.service.SubscribeUserService;
import com.ps2site.util.ServerConstants;
import com.yilnz.qqbotlib.QQBot;
import com.yilnz.qqbotlib.entity.FriendMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Component
@Slf4j
public class MsgCmd {
    @Autowired
    private SubscribeUserService subscribeUserService;
    @Autowired
    private QQBotConfig qqBotConf;

    private ConcurrentHashMap<String, ContextStatus> context = new ConcurrentHashMap<>();

    public String[] onReceiveQQMsg(FriendMessage msg) {
        if(msg.getSender().getId().equals(qqBotConf.getQqNumber())){
            return null;
        }
        String[] ret = null;
        switch (msg.getMessage()) {
            case "订阅":
                List<String> tempList = new ArrayList<>();
                AtomicInteger atomicInteger = new AtomicInteger();
                List<SubscribeUser> userss = subscribeUserService.getUsers(msg.getSender().getId());
                tempList.add("你当前的订阅服务器：");
                tempList.addAll(userss.stream().map(SubscribeUser::getServer).collect(Collectors.toList()));
                tempList.add("输入序号选择你要订阅服务器:");
                tempList.addAll(ServerConstants.getServerNames().stream().map(e -> {
                    return atomicInteger.incrementAndGet() + "." + e;
                }).collect(Collectors.toList()));
                ret = tempList.toArray(new String[]{});
                context.put(msg.getSender().getId(), ContextStatus.DESCRIBING);
                break;
            case "取消订阅":
                List<SubscribeUser> users = subscribeUserService.getUsers(msg.getSender().getId());
                List<String> tem = new ArrayList<>();
                tem.add("请输入取消订阅序号：");
                AtomicInteger atomicInteger2 = new AtomicInteger();
                tem.addAll(users.stream().map(u -> {
                    return atomicInteger2.incrementAndGet() + "." + u.getServer();
                }).collect(Collectors.toList()));
                ret = tem.toArray(new String[]{});
                context.put(msg.getSender().getId(), ContextStatus.CANCELING_SUBSCRIBE);
                break;
            case "状态":
                List<SubscribeUser> users3 = subscribeUserService.getUsers(msg.getSender().getId());
                List<String> tem2 = new ArrayList<>();
                tem2.add("你当前的订阅服务器：");
                tem2.addAll(users3.stream().map(SubscribeUser::getServer).collect(Collectors.toList()));
                ret = tem2.toArray(new String[]{});
                break;
            case "用户列表":
                List<String> tem4 = new ArrayList<>();
                List<SubscribeUser> users1 = subscribeUserService.getUsers();
                tem4.add("当前用户列表：");
                tem4.addAll(users1.stream().map(u -> {
                    return "QQ:" + u.getQq() + ",Server:" + u.getServer() + ",Email:" + u.getEmail() + ",isQQGroup:" + u.getIsQQGroup();
                }).collect(Collectors.toList()));
                ret = tem4.toArray(new String[]{});
                break;
            default:
                boolean clearStatus = true;
                try {
                    ContextStatus contextStatus = this.context.get(msg.getSender().getId());
                    if (ContextStatus.DESCRIBING.equals(contextStatus)) {
                        Matcher matcher = Pattern.compile("\\d").matcher(msg.getMessage());
                        if (matcher.find()) {
                            int index = Integer.parseInt(msg.getMessage()) - 1;
                            if (index >= ServerConstants.getServerNames().size()) {
                                ret = new String[]{"取值范围为 1-" + ServerConstants.getServerNames().size() + " ,请重新订阅"};
//                                clearStatus = false;
                            } else {
                                String server = ServerConstants.getServerNames().get(index);
                                SubscribeUser subscribeUser =
                                        new SubscribeUser(null, server, null, msg.getSender().getId(), false);
                                subscribeUserService.addUser(subscribeUser);
                                ret = new String[]{"订阅成功，订阅的服务器：" + server};
                            }
                        } else {
                            ret = new String[]{"范围 1-" + ServerConstants.getServerNames().size() + " ,请重新订阅"};
//                            clearStatus = false;
                        }
                    } else if (ContextStatus.CANCELING_SUBSCRIBE.equals(contextStatus)) {
                        Matcher matcher = Pattern.compile("\\d").matcher(msg.getMessage());
                        List<SubscribeUser> users2 = subscribeUserService.getUsers(msg.getSender().getId());
                        if (matcher.find()) {
                            int index = Integer.parseInt(msg.getMessage()) - 1;
                            if (index >= users2.size()) {
                                ret = new String[]{"取值范围为 1-" + users2.size() + " ,请重新取消订阅"};
//                                clearStatus = false;
                            } else {
                                SubscribeUser subscribeUser = users2.get(index);
                                subscribeUserService.delete(subscribeUser.getEmail(), subscribeUser.getQq(), subscribeUser.getServer());
                                ret = new String[]{"取消订阅成功，取消订阅的服务器：" + subscribeUser.getServer()};
                            }
                        } else {
                            ret = new String[]{"取值范围为 1-" + users2.size() + " ,请重新取消订阅"};
                            //clearStatus = false;
                        }
                    } else {
                        ret = new String[]{"可用命令：帮助、订阅、取消订阅、状态"};
                    }
                }catch (BizException e){
                    ret = new String[]{e.getMessage()};
                } catch (Exception exception) {
                    log.error("QQ消息接收出问题了", exception);
                    ret = new String[]{"出现了某种错误，请联系管理员"};
                } finally {
                    if (clearStatus) {
                        context.put(msg.getSender().getId(), ContextStatus.NONE);
                    }
                }
        }
        return Arrays.stream(ret).map(e->e + "\n").collect(Collectors.toList()).toArray(new String[]{});
    }
}
