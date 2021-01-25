package com.u8.server.utils;

import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.UHttpAgent;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UGenerator {

    /***
     * 生成appkey
     * @return
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

//    public static void main(String[] args){
//        String t = "http://121.41.41.162:8080/server/user/verifyAccount";
//        Map<String, String> params = new HashMap<>();
//        params.put("token", "69D77DCEAD825024CE8C2A5CBD8477DD");
//        params.put("userID", "1");
//        params.put("sign", "8212f0caded24a2d8ca0d4020b4035a2");
//        String o = UHttpAgent.getInstance().post(t, params);
//        System.out.println(o);
//    }

}
