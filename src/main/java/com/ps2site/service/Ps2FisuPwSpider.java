package com.ps2site.service;

import com.ps2site.domain.XmlItem;
import com.ps2site.util.ServerConstants;
import com.yilnz.surfing.core.SurfHttpRequestBuilder;
import com.yilnz.surfing.core.SurfSpider;
import com.yilnz.surfing.core.basic.Page;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;
import us.codecraft.xsoup.XElements;
import us.codecraft.xsoup.Xsoup;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * https://ps2.fisu.pw/
 */
@Component
public class Ps2FisuPwSpider implements AlertSpider {


    //for test
    public static void main(String[] args) {
        boolean connery = new Ps2FisuPwSpider().isAlertStarted("Connery", new HashMap<>());
        System.out.println(connery);
    }

    @Override
    public boolean isAlertStarted(String serverName, Map<String, String> models) {

        Map<String, Integer> serverMappings = new HashMap<>();
        serverMappings.put(ServerConstants.SolTech, 40);
        serverMappings.put(ServerConstants.Emerald, 17);
        serverMappings.put(ServerConstants.Connery, 1);
        serverMappings.put(ServerConstants.Miller, 10);
        serverMappings.put(ServerConstants.Cobalt, 13);
        Page page = SurfSpider.create().addRequest(
                SurfHttpRequestBuilder.create("https://ps2.fisu.pw/alert/rss/?world=" + serverMappings.get(serverName), "GET").build())
                .request().get(0);
        String rssString = page.getHtml().get();
        XElements items = Xsoup.select(rssString, "channel/item");
        Elements elements = items.getElements();
        List<XmlItem> collect = elements.stream().map(item -> {
            XmlItem xmlItem = new XmlItem();
            xmlItem.setDescription(item.selectFirst("description").text());
            xmlItem.setPubDate(item.selectFirst("pubDate").text());
            xmlItem.setEnd(item.getElementsByTag("fisupw:duration").size() > 0);
            xmlItem.setDuration(xmlItem.isEnd() ? Long.valueOf(item.getElementsByTag("fisupw:duration").get(0).text()) : 0L);
            return xmlItem;
        }).collect(Collectors.toList());
        if (models != null) {
            models.put("description", collect.get(0).getDescription());
            models.put("duration", String.valueOf(collect.get(0).getDuration()));
            models.put("pubDate", collect.get(0).getPubDate());
        }
        return !collect.get(0).isEnd();
    }
}
