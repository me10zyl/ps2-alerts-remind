package com.ps2site.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ps2site.domain.ApiResult;
import com.ps2site.domain.SubscribeUser;
import com.ps2site.exception.BizException;
import com.ps2site.service.SubscribeUserService;
import com.ps2site.util.ServerConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/")
public class MainController {

    @Value("${pwd.user.list}:ps2alert")
    private String listUserPwd;

    @Autowired
    private SubscribeUserService subscribeUserService;

    @ExceptionHandler(Exception.class)
    public void exceptionHandle(Exception e, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println(new ObjectMapper().writeValueAsString(ApiResult.fail(e.getMessage(), null)));
    }

    @RequestMapping("/user/add")
    @ResponseBody
    public ApiResult addUser(@RequestBody SubscribeUser user) {
        if (ServerConstants.getServerNames().stream().noneMatch(e -> e.equals(user.getServer()))) {
            throw new BizException("服务器仅能为：" + String.join(",", ServerConstants.getServerNames()));
        }
        subscribeUserService.addUser(user);
        return ApiResult.success("添加成功", null);
    }


    @RequestMapping("/user/list")
    @ResponseBody
    public ApiResult listUsers(String pwd) {
        if (!listUserPwd.equals(pwd)) {
            throw new BizException("输入正确的密码");
        }
        List<SubscribeUser> users = subscribeUserService.getUsers();
        return ApiResult.success("查询成功", users);
    }

    @RequestMapping("/user/delete")
    @ResponseBody
    public ApiResult deleteUser(String email, String server, String qq) {
        boolean delete = subscribeUserService.delete(email, qq, server);
        if (delete) {
            return ApiResult.success("删除成功", null);
        }
        throw new BizException("查无此人");
    }

}
