package com.u8.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 全局配置
 */
@Component("globalConfig")
@Scope("singleton")
public class GlobalConfig {

    @Value("${u8server.base_url}")
    private String baseUrl;

    @Value("${u8server.deploy_id}")
    private int deployID;

    @Value("${u8server.pay_sign_method}")
    private String paySignMethod;           //通知回调通知游戏服务器采用的签名算法， MD5或者RSA

    @Value("${u8server.pay_content_type}")
    private String payContentType;          //通知回调通知游戏服务器采用的Content-Type，application/x-www-form-urlencoded或者application/json

    @Value("${u8server.blog}")
    private Boolean printBLog;                   //是否开启业务日志记录

    public String getBaseUrl() {
        return baseUrl;
    }

    public int getDeployID() {
        return deployID;
    }

    public String getPaySignMethod() {
        return paySignMethod;
    }

    public String getPayContentType() {
        return payContentType;
    }

    public boolean isPringBLog() {
        return printBLog;
    }
}
