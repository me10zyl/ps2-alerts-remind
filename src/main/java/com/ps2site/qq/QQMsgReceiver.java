package com.ps2site.qq;

import com.yilnz.qqbotlib.QQBot;
import com.yilnz.qqbotlib.entity.FriendMessage;
import com.yilnz.qqbotlib.entity.NewFriendRequest;
import com.yilnz.qqbotlib.entity.NewFriendRequestHandleResult;
import com.yilnz.qqbotlib.entity.QQMessage;
import com.yilnz.qqbotlib.listeners.QQEventListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
@Slf4j
public class QQMsgReceiver implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private QQBot qqBot;
    @Autowired
    private MsgCmd msgCmd;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        log.info("开启QQ事件接收...");
        qqBot.onMessageReceived(new QQEventListener() {

            @Override
            public NewFriendRequestHandleResult onReceivedNewFriendRequest(NewFriendRequest request) {
                log.info("收到了好友请求：" + request.getFromId());
                NewFriendRequestHandleResult newFriendRequestHandleResult = new NewFriendRequestHandleResult();
                newFriendRequestHandleResult.setAccept(true);
                return newFriendRequestHandleResult;
            }

            @Override
            public void onReceivedFriendMessage(FriendMessage friendMessage) {
                log.info("收到了好友消息：" + friendMessage.getSender().getId() + ":" + friendMessage.getMessage());
                String[] replyList = msgCmd.onReceiveQQMsg(friendMessage);
                List<QQMessage> qqMessageList = Arrays.stream(replyList).map(QQMessage::textMessage).collect(Collectors.toList());
                qqBot.sendFriendMessage(friendMessage.getSender().getId(), qqMessageList);
            }
        });
    }
}
