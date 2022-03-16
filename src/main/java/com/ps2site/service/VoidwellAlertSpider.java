package com.ps2site.service;

import org.springframework.stereotype.Component;

import java.util.Map;


public class VoidwellAlertSpider implements AlertSpider{

    @Override
    public boolean isAlertStarted(String serverName, Map<String, String> models) {
        return false;
    }
}
