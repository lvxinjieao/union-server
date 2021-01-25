package com.u8.server.data;

import com.u8.server.cache.CacheManager;
import net.sf.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 渠道对象(每个游戏对某个渠道)
 */

@Entity
@Table(name = "uchannel")
public class UChannel implements Serializable{


    private static final long serialVersionUID = 3341253418352932944L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;                 //数据表中ID

    private int channelID;              //渠道ID 和客户端一致
    private Integer uniChannel;            //互通渠道号， 互通渠道号一致的渠道， 账号ID互通，前提是渠道SDK本身是互通的
    private int appID;                  //游戏ID
    private int masterID;               //渠道商ID

    private String cpID;                //渠道分配给游戏的cpID
    private String cpAppID;             //渠道分配给游戏的appID
    private String cpAppKey;            //渠道分配给游戏的AppKey
    private String cpAppSecret;         //渠道分配给游戏的AppSecret

    private String cpPayID;             //渠道分配给游戏的支付ID
    private String cpPayKey;            //渠道分配给游戏的支付公钥
    private String cpPayPriKey;         //渠道分配给游戏的支付私钥

    private String cpConfig;            //部分渠道可能有特殊配置信息，设置在该字段中

    private Integer openPayFlag;            //充值功能状态，0：开放；1：关闭。关闭之后，该渠道获取订单号会返回一个错误

    private String channelParams;       //渠道参数配置(json格式)


    //下面这几个参数，可以覆盖uchannelmaster中的。
    private String authUrl;         //当前SDK登录认证地址
    private String payCallbackUrl;  //当前SDK支付通知回调地址
    private String verifyClass;     //当前SDK的验证处理类的全类名
    private String orderUrl;        //SDK订单号获取地址，没有则为空

    public void copy(UChannel channel){
        this.channelID = channel.getChannelID();
        this.uniChannel = channel.getUniChannel();
        this.appID = channel.getAppID();
        this.masterID = channel.getMasterID();
        this.cpID = channel.getCpID();
        this.cpAppID = channel.getCpAppID();
        this.cpAppKey = channel.getCpAppKey();
        this.cpAppSecret = channel.getCpAppSecret();
        this.cpPayID = channel.getCpPayID();
        this.cpPayKey = channel.getCpPayKey();
        this.cpPayPriKey = channel.getCpPayPriKey();
        this.cpConfig = channel.getCpConfig();
        this.openPayFlag = channel.getOpenPayFlag();
        this.authUrl = channel.getAuthUrl();
        this.payCallbackUrl = channel.getPayCallbackUrl();
        this.verifyClass = channel.getVerifyClass();
        this.orderUrl = channel.getOrderUrl();
    }


    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("channelID", channelID);
        json.put("uniChannel", uniChannel);
        json.put("appID", appID);
        UGame game = CacheManager.getInstance().getGame(appID);

        json.put("appName", game == null ? "":game.getName());
        json.put("masterID", masterID);
        UChannelMaster master = CacheManager.getInstance().getMaster(masterID);


        json.put("masterName", master == null ? "" : master.getMasterName());
        json.put("cpID", cpID);
        json.put("cpAppID", cpAppID);
        json.put("cpAppKey", cpAppKey);
        json.put("cpAppSecret", cpAppSecret);
        json.put("cpPayID", cpPayID);
        json.put("cpPayKey", cpPayKey);
        json.put("cpPayPriKey", cpPayPriKey);
        json.put("cpConfig",cpConfig);
        json.put("openPayFlag", openPayFlag);
        json.put("authUrl", authUrl);
        json.put("payCallbackUrl", payCallbackUrl);
        json.put("verifyClass", verifyClass);
        json.put("orderUrl", orderUrl);

        return json;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getChannelID() {
        return channelID;
    }

    public void setChannelID(int channelID) {
        this.channelID = channelID;
    }

    public int getAppID() {
        return appID;
    }

    public void setAppID(int appID) {
        this.appID = appID;
    }

    public int getMasterID() {
        return masterID;
    }

    public void setMasterID(int masterID) {
        this.masterID = masterID;
    }

    public String getCpID() {
        return cpID;
    }

    public void setCpID(String cpID) {
        this.cpID = cpID;
    }

    public String getCpAppID() {
        return cpAppID;
    }

    public void setCpAppID(String cpAppID) {
        this.cpAppID = cpAppID;
    }

    public String getCpAppKey() {
        return cpAppKey;
    }

    public void setCpAppKey(String cpAppKey) {
        this.cpAppKey = cpAppKey;
    }

    public String getCpPayKey() {
        return cpPayKey;
    }

    public void setCpPayKey(String cpPayKey) {
        this.cpPayKey = cpPayKey;
    }

    public String getCpAppSecret() {
        return cpAppSecret;
    }

    public void setCpAppSecret(String cpAppSecret) {
        this.cpAppSecret = cpAppSecret;
    }

    public String getCpConfig() {
        return cpConfig;
    }

    public void setCpConfig(String cpConfig) {
        this.cpConfig = cpConfig;
    }

    public String getCpPayID() {
        return cpPayID;
    }

    public void setCpPayID(String cpPayID) {
        this.cpPayID = cpPayID;
    }

    public String getCpPayPriKey() {
        return cpPayPriKey;
    }

    public void setCpPayPriKey(String cpPayPriKey) {
        this.cpPayPriKey = cpPayPriKey;
    }

    public String getAuthUrl() {
        return authUrl;
    }

    public void setAuthUrl(String authUrl) {
        this.authUrl = authUrl;
    }

    public void setPayCallbackUrl(String payCallbackUrl) {
        this.payCallbackUrl = payCallbackUrl;
    }

    public String getVerifyClass() {
        return verifyClass;
    }

    public void setVerifyClass(String verifyClass) {
        this.verifyClass = verifyClass;
    }

    public String getOrderUrl() {
        return orderUrl;
    }

    public void setOrderUrl(String orderUrl) {
        this.orderUrl = orderUrl;
    }

    public Integer getOpenPayFlag() {
        return openPayFlag;
    }

    public void setOpenPayFlag(Integer openPayFlag) {
        this.openPayFlag = openPayFlag;
    }

    public String getPayCallbackUrl() {
        return payCallbackUrl;
    }

    public String getChannelParams() {
        return channelParams;
    }

    public void setChannelParams(String channelParams) {
        this.channelParams = channelParams;
    }

    public Integer getUniChannel() {
        return uniChannel;
    }

    public void setUniChannel(Integer uniChannel) {
        this.uniChannel = uniChannel;
    }
}
