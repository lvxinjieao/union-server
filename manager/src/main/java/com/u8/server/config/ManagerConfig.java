package com.u8.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * 管理后台配置
 * Created by ant on 2016/12/21.
 */
@Component("managerConfig")
@Scope("singleton")
public class ManagerConfig {

    @Value("${u8server.apiID}")
    private String apiID;                   //打包工具中请求api协议的时候使用。

    @Value("${u8server.apiKey}")
    private String apiKey;                  //打包工具中请求api协议的时候使用。

    public String getApiID(){
        return apiID;
    }

    public String getApiKey(){
        return apiKey;
    }
}
