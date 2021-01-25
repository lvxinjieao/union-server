package com.u8.server.data;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.u8.server.cache.CacheManager;
import com.u8.server.cache.UApplicationContext;
import com.u8.server.config.GlobalConfig;

import com.u8.server.utils.StringUtils;

import javax.persistence.*;
import java.io.Serializable;
import java.lang.String;
import java.util.Map;

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

    private Integer openPayFlag;        //充值功能状态，0：开放；1：关闭。关闭之后，该渠道获取订单号会返回一个错误

    private String channelParams;       //渠道参数配置(json格式)


    //下面这几个参数，可以覆盖uchannelmaster中的。
    private String authUrl;         //当前SDK登录认证地址
    private String payCallbackUrl;  //当前SDK支付通知回调地址
    private String verifyClass;     //当前SDK的验证处理类的全类名
    private String orderUrl;        //SDK订单号获取地址，没有则为空

    @JSONField(serialize=false)
    public UGame getGame(){return CacheManager.getInstance().getGame(appID);}

    @JSONField(serialize=false)
    public UChannelMaster getMaster(){
        return CacheManager.getInstance().getMaster(masterID);
    }

    //根据key获取配置的渠道参数
    @JSONField(serialize=false)
    public String getChannelParamByKey(String key){

        if(StringUtils.isEmpty(key) || StringUtils.isEmpty(channelParams)){

            return null;
        }

        JSONObject jsonObject = JSON.parseObject(channelParams);
        if(jsonObject == null) return null;

        return jsonObject.getString(key);

    }

    //获取渠道参数
    @JSONField(serialize=false)
    public Map<String,Object> getChannelParamMap(){
        if(StringUtils.isEmpty(channelParams)){

            return null;
        }

        JSONObject jsonObject = JSON.parseObject(channelParams);
        if(jsonObject == null) return null;

        return (Map<String, Object>)jsonObject;

    }


    //获取当前渠道的登录认证地址
    @JSONField(serialize=false)
    public String getChannelAuthUrl(){
        if(this.authUrl == null || this.authUrl.trim().length() == 0){
            return  getMaster().getAuthUrl().trim();
        }
        return this.authUrl == null ? "" : this.authUrl.trim();
    }

    //获取当前渠道的SDK处理类的名称
    @JSONField(serialize=false)
    public String getChannelVerifyClass(){
        if(this.verifyClass == null || this.verifyClass.trim().length() == 0){
            return getMaster().getVerifyClass().trim();
        }
        return this.verifyClass.trim();
    }

    //获取当前渠道的SDK处理类的下单地址
    @JSONField(serialize=false)
    public String getChannelOrderUrl() {
        if (this.orderUrl == null || this.orderUrl.trim().length() == 0) {
            return getMaster().getOrderUrl().trim();
        }
        return this.orderUrl.trim();
    }

    //获取当前渠道的支付回调地址
    @JSONField(serialize=false)
    public String getPayCallbackUrl(){
        String baseUrl = getMaster().getPayCallbackUrl().trim();
        if(this.payCallbackUrl != null && this.payCallbackUrl.trim().length() > 0){
            baseUrl = this.payCallbackUrl.trim();
        }

        if (!baseUrl.startsWith("http:") && !baseUrl.startsWith("https")){

            //加上根地址
            if(!baseUrl.startsWith("/")){
                baseUrl = "/" + baseUrl;
            }

            GlobalConfig config = (GlobalConfig)UApplicationContext.getBean("globalConfig");

            String rootUrl = config.getBaseUrl();

            if(rootUrl.endsWith("/")){
                rootUrl = rootUrl.substring(0, rootUrl.length()-1);
            }

            baseUrl = rootUrl + baseUrl;

        }

        if(baseUrl.contains(""+channelID)){
            return baseUrl;
        }

        if (!baseUrl.endsWith("/")){
            baseUrl += "/";
        }
        return baseUrl + channelID;
    }

    //当前渠道是否开放充值
    @JSONField(serialize=false)
    public boolean isPayOpen(){

        return this.openPayFlag == 0;
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
        cpID = (cpID==null ? null : cpID.trim());
        this.cpID = cpID;
    }

    public String getCpAppID() {
        return cpAppID;
    }

    public void setCpAppID(String cpAppID) {
        cpAppID = (cpAppID==null ? null : cpAppID.trim());
        this.cpAppID = cpAppID;
    }

    public String getCpAppKey() {
        return cpAppKey;
    }

    public void setCpAppKey(String cpAppKey) {
        cpAppKey = (cpAppKey==null ? null : cpAppKey.trim());
        this.cpAppKey = cpAppKey;
    }

    /**支付公钥*/
    public String getCpPayKey() {
        return cpPayKey;
    }

    public void setCpPayKey(String cpPayKey) {
        cpPayKey = (cpPayKey==null ? null : cpPayKey.trim());
        this.cpPayKey = cpPayKey;
    }

    public String getCpAppSecret() {
        return cpAppSecret;
    }

    public void setCpAppSecret(String cpAppSecret) {
        cpAppSecret = (cpAppSecret==null ? null : cpAppSecret.trim());
        this.cpAppSecret = cpAppSecret;
    }

    public String getCpConfig() {
        return cpConfig;
    }

    public void setCpConfig(String cpConfig) {
        cpConfig = (cpConfig == null ? null : cpConfig.trim());
        this.cpConfig = cpConfig;
    }

    public String getCpPayID() {
        return cpPayID;
    }

    public void setCpPayID(String cpPayID) {
        this.cpPayID = cpPayID;
    }

    /**支付私钥*/
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
