package com.ps2site.service;

import java.util.Map;

public interface AlertSpider {

    boolean isAlertStarted(String serverName, Map<String, String> models);

}
