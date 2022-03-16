package com.ps2site.domain;

import lombok.Data;

@Data
public class XmlItem {
    private String pubDate;
    private boolean isEnd;
    private String title;
    private String description;
    private Long duration;
}
