package com.ps2site.qq;

import com.yilnz.qqbotlib.QQBot;
import com.yilnz.qqbotlib.QQMessageListener;
import com.yilnz.qqbotlib.entity.FriendMessage;
import com.yilnz.qqbotlib.entity.QQMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class QQMsgReceiver implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private QQBot qqBot;
    @Autowired
    private MsgCmd msgCmd;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        qqBot.onMessageReceived(new QQMessageListener() {
            @Override
            public void onReceivedFirendMessage(FriendMessage friendMessage) {
                String[] replyList = msgCmd.onReceiveQQMsg(friendMessage);
                List<QQMessage> qqMessageList = Arrays.asList(replyList).stream().map(e -> {
                    QQMessage qqMessage = QQMessage.textMessage(e);
                    return qqMessage;
                }).collect(Collectors.toList());
                qqBot.sendFriendMessage(friendMessage.getSender().getId(), qqMessageList);
            }
        });
    }
}
