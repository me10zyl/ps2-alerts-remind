package com.ps2site.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ps2site.domain.ApiResult;
import com.ps2site.domain.SubscribeUser;
import com.ps2site.exception.BizException;
import com.ps2site.service.SubscribeUserService;
import com.ps2site.util.ServerConstants;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/")
public class MainController {

    @Autowired
    private SubscribeUserService subscribeUserService;

    @ExceptionHandler(Exception.class)
    public void exceptionHandle(Exception e, HttpServletResponse response) throws IOException {
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().println(new ObjectMapper().writeValueAsString(ApiResult.fail(e.getMessage(), null)));
    }

    @RequestMapping("/user/add")
    @ResponseBody
    public ApiResult addUser(String email, String server, @RequestParam(required = false) String qq){
        if(ServerConstants.getServerNames().stream().noneMatch(e->e.equals(server))){
            throw new BizException("服务器仅能为：" + String.join(",", ServerConstants.getServerNames()));
        }
        subscribeUserService.addUser(new SubscribeUser(email, server, null,qq));
        return ApiResult.success("添加成功", null);
    }


    @RequestMapping("/user/list")
    @ResponseBody
    public ApiResult listUsers(){
        List<SubscribeUser> users = subscribeUserService.getUsers();
        return ApiResult.success("查询成功", users);
    }

    @RequestMapping("/user/delete")
    @ResponseBody
    public ApiResult deleteUser(String email, String server){
        boolean delete = subscribeUserService.delete(email, server);
        if(delete){
            return ApiResult.success("删除成功", null);
        }
        throw new BizException("查无此人");
    }

}
