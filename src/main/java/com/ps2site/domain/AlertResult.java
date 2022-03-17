package com.ps2site.domain;

import lombok.Data;

import java.util.Date;

@Data
public class AlertResult {
    private boolean isStarted;
    private Date alertStartTime;
    private Date alertEndTime;
    private Long duration;

    //extra
    private String description;
    private String pubDate;
}
