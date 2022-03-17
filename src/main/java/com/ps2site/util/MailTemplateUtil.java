package com.ps2site.util;

import java.util.Map;

public class MailTemplateUtil {

    private Map<String, Object> variables;

    public MailTemplateUtil(Map<String, Object> variables) {
        this.variables = variables;
    }

    public  String getTitle(){
        return replaceVariables("奥拉西斯之{server}警报已经在{alertStartTimeFormat}开始了");
    }

    public String getQQMessage(){
        return replaceVariables(getTitle() + "," + getContent());
    }

    private String replaceVariables(String str) {
        StringBuilder s = new StringBuilder(str);
        variables.forEach((k,v)->{
            String newString = s.toString().replaceAll("\\{" + k + "\\}", String.valueOf(v));
            s.delete(0, s.length());
            s.append(newString);
        });
        return s.toString();
    }


    public  String getContent(){
        return replaceVariables("快去征服奥拉西斯:{description}");
    }
}
