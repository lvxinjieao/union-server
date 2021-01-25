package com.u8.server.utils;

import com.u8.server.data.UUser;
import com.u8.server.log.Log;

import java.util.UUID;

public class UGenerator {

    /***
     * 生成appkey
     */
    public static String generateAppKey(){

        UUID uuid = UUID.randomUUID();
        String txt = uuid.toString().replace("-","");

        return txt;
    }

    /***
     * 生成appsecret
     *
     * @return
     */
    public static String generateAppSecret(){
        UUID uuid = UUID.randomUUID();
        return uuid.toString().replace("-", "");
    }

    /***
     * 生成用于生成登录认证使用的Token
     * @param user
     * @return
     */
    public static String generateToken(UUser user, String appSecret){

        StringBuilder sb = new StringBuilder();
        sb.append(user.getId()).append("-")
                .append(user.getChannelUserID()).append("-")
                .append(user.getLastLoginTime().getTime())
                .append(appSecret);

        String txt = sb.toString();

        Log.d("The txt to generate token is " + txt);
        return EncryptUtils.md5(txt);
    }


}
