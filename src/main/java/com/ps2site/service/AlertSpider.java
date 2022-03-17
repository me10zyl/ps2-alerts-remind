package com.ps2site.service;

import com.ps2site.domain.AlertResult;

import java.util.Map;

public interface AlertSpider {

    AlertResult isAlertStarted(String serverName);

}
