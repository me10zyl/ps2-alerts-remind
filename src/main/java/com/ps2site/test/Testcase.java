package com.ps2site.test;

import com.ps2site.domain.SubscribeUser;
import com.ps2site.exception.BizException;
import com.ps2site.service.SubscribeUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@Component
@ConditionalOnProperty({"test.start"})
public class Testcase {

    @Autowired
    private SubscribeUserService subscribeUserService;

    @PostConstruct
    public void tests(){
        try {
            test2();
            test1();
        } catch (BizException e) {
            //e.printStackTrace();
        }
    }


    public void test2(){

    }


    public void test1(){
        System.out.println(subscribeUserService.getUsers());
    }
}
