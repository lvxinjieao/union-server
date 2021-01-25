package com.u8.server.data;

import com.alibaba.fastjson.annotation.JSONField;
import net.sf.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 游戏对象
 */
@Entity
@Table(name = "ugame")
public class UGame implements Serializable{

    private static final long serialVersionUID = -7431124945608619219L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer appID;          //每款游戏唯一ID

    private Integer cpID;           //提供商ID
    private String appkey;          //根据appID等信息md5生成的游戏唯一标识
    private String appSecret;       //游戏服务器端和U8Server生成token使用。不要将该参数放在客户端
    private String appRSAPubKey;    //RSA公钥 在登录认证和支付回调的时候使用RSA签名
    private String appRSAPriKey;    //RSA密钥
    private String name;            //游戏名称
    private long createTime;        //创建时间
    private String payCallback;     //支付回调地址
    private String msdkPayCallback; //应用宝回调地址
    private String payCallbackDebug;//支付回调地址，调试
    private Integer isSingle;         //是否纯单机，无游戏服务器。1：纯单机

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("cpID", cpID);
        json.put("appID", appID);
        json.put("appkey", appkey);
        json.put("appSecret", appSecret);
        json.put("appRSAPubKey", appRSAPubKey);
        json.put("appRSAPriKey", appRSAPriKey);
        json.put("name", name);
        json.put("payCallback", payCallback);
        json.put("msdkPayCallback", msdkPayCallback);
        json.put("payCallbackDebug", payCallbackDebug);
        json.put("isSingle", isSingle == null ? 0 : isSingle);

        return json;
    }

    @JSONField(serialize=false)
    public boolean isSingleGame(){
        return isSingle != null && isSingle == 1;
    }

    public Integer getCpID() {
        return cpID;
    }

    public void setCpID(Integer cpID) {
        this.cpID = cpID;
    }

    public String getAppSecret() {
        return appSecret;
    }

    public void setAppSecret(String appSecret) {
        this.appSecret = appSecret;
    }

    public Integer getAppID() {
        return appID;
    }

    public void setAppID(Integer appID) {
        this.appID = appID;
    }

    public String getAppkey() {
        return appkey;
    }

    public void setAppkey(String appkey) {
        this.appkey = appkey;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPayCallback() {
        return payCallback;
    }

    public void setPayCallback(String payCallback) {
        this.payCallback = payCallback;
    }

    public String getPayCallbackDebug() {
        return payCallbackDebug;
    }

    public void setPayCallbackDebug(String payCallbackDebug) {
        this.payCallbackDebug = payCallbackDebug;
    }

    public String getAppRSAPubKey() {
        return appRSAPubKey;
    }

    public void setAppRSAPubKey(String appRSAPubKey) {
        this.appRSAPubKey = appRSAPubKey;
    }

    public String getAppRSAPriKey() {
        return appRSAPriKey;
    }

    public void setAppRSAPriKey(String appRSAPriKey) {
        this.appRSAPriKey = appRSAPriKey;
    }

    public String getMsdkPayCallback() {
        return msdkPayCallback;
    }

    public void setMsdkPayCallback(String msdkPayCallback) {
        this.msdkPayCallback = msdkPayCallback;
    }

    public Integer getIsSingle() {
        return isSingle;
    }

    public void setIsSingle(Integer isSingle) {
        this.isSingle = isSingle;
    }
}
