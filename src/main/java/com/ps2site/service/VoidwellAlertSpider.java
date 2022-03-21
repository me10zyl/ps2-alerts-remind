package com.ps2site.service;

import cn.hutool.core.date.DateUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.ps2site.domain.AlertResult;
import com.ps2site.util.ServerConstants;
import com.yilnz.surfing.core.SurfHttpRequest;
import com.yilnz.surfing.core.SurfHttpRequestBuilder;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Page;
import com.yilnz.surfing.core.header.generators.CurlHeaderGenerator;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * https://voidwell.com/ps2/alerts
 */
@Component
public class VoidwellAlertSpider implements AlertSpider{

    @Override
    public AlertResult isAlertStarted(String serverName) {
        Map<String, Integer> serverMappings = new HashMap<>();
        serverMappings.put(ServerConstants.SolTech, 40);
        serverMappings.put(ServerConstants.Emerald, 17);
        serverMappings.put(ServerConstants.Connery, 1);
        serverMappings.put(ServerConstants.Miller, 10);
        serverMappings.put(ServerConstants.Cobalt, 13);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssX");
        SurfHttpRequest r = new SurfHttpRequestBuilder("https://api.voidwell.com/ps2/alert/alerts/0?platform=pc", "GET").build();
        r.setHeaderGenerator(new CurlHeaderGenerator("curl 'https://api.voidwell.com/ps2/world?platform=pc' \\\n" +
                "  -H 'authority: api.voidwell.com' \\\n" +
                "  -H 'sec-ch-ua: \" Not A;Brand\";v=\"99\", \"Chromium\";v=\"98\", \"Google Chrome\";v=\"98\"' \\\n" +
                "  -H 'accept: application/json, text/plain, */*' \\\n" +
                "  -H 'sec-ch-ua-mobile: ?0' \\\n" +
                "  -H 'user-agent: Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/98.0.4758.82 Safari/537.36' \\\n" +
                "  -H 'sec-ch-ua-platform: \"Windows\"' \\\n" +
                "  -H 'origin: https://voidwell.com' \\\n" +
                "  -H 'sec-fetch-site: same-site' \\\n" +
                "  -H 'sec-fetch-mode: cors' \\\n" +
                "  -H 'sec-fetch-dest: empty' \\\n" +
                "  -H 'referer: https://voidwell.com/' \\\n" +
                "  -H 'accept-language: zh-CN,zh;q=0.9,en;q=0.8' \\\n" +
                "  --compressed"));
        r.setConnectTimeout(10000);
        Page page = SurfSpider.create().addRequest(r)
                .request().get(0);
        Optional<Object> alerts =  ((JSONArray) JSONArray.parseArray(page.getHtml().get())).stream().filter(item -> {
            if (!((JSONObject) item).getInteger("worldId").equals(serverMappings.get(serverName))) {
                return false;
            }
            Date endDate = null;
            try {
                endDate = sdf.parse(((JSONObject) item).getString("endDate"));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return endDate.compareTo(new Date()) > 0;
        }).findFirst();
        AlertResult alertResult = new AlertResult();
        if(alerts.isPresent()) {
            JSONObject alert = (JSONObject) alerts.get();
            alertResult.setStarted(true);
            try {
                alertResult.setAlertStartTime(sdf.parse(alert.getString("startDate")));
                alertResult.setAlertEndTime(sdf.parse(alert.getString("endDate")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            alertResult.setDuration( (alertResult.getAlertEndTime().getTime() - alertResult.getAlertStartTime().getTime()) /1000);
            JSONObject metagameEvent = alert.getJSONObject("metagameEvent");
            if (metagameEvent != null) {
                alertResult.setDescription(metagameEvent.getString("description"));
            }
            alertResult.setPubDate(DateUtil.format(alertResult.getAlertStartTime(), "yyyy-MM-dd HH:mm:ss"));
        }
        //.in-progress .alert-card
        return alertResult;
    }

    public static void main(String[] args) {
        AlertResult alertStarted = new VoidwellAlertSpider().isAlertStarted("Emerald");
        System.out.println(alertStarted);
    }
}
