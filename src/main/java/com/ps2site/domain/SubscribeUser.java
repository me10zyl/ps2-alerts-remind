package com.ps2site.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
@AllArgsConstructor
public class SubscribeUser {

    private String email;
    private String server;
    private Date alertStartTime;
    private String qq;
    @TableField("is_qq_group")
    private Boolean isQQGroup;
}
