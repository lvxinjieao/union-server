package com.u8.server.data;

import com.alibaba.fastjson.annotation.JSONField;
import com.u8.server.cache.CacheManager;
import com.u8.server.utils.TimeUtils;
import net.sf.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * 用户数据对象
 */

@Entity
@Table(name = "uuser")
public class UUser implements Serializable{

    private static final long serialVersionUID = -5667044567496125167L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;         //主键，唯一ID
    private int appID;          //当前用户所属游戏ID
    private int channelID;      //当前用户所属渠道ID
    private int uniChannel;     //互通渠道号

    @Column(name = "subChannelID", nullable = false, columnDefinition = "int default 0")
    private int subChannelID;   //子渠道号

    private String name;        //用户名称
    private String channelUserID;   //渠道方用户ID
    private String channelUserName; //渠道方用户名
    private String channelUserNick; //渠道方用户昵称
    private Date createTime;        //创建时间
    private Date lastLoginTime;     //最后登录时间
    private String token;           //登录token
    private Integer firstCharge;        //是否充值过(没有：0；充值过：1)
    private Date firstChargeTime;       //首冲时间
    private String deviceID;            //设备ID

    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("appID", appID);


        UGame game = getGame();

        json.put("appName", game == null ? "":game.getName());
        json.put("channelID", channelID);

//        UChannel channel = getChannel();
//        json.put("channelName", channel == null ? "":channel.getMaster().getMasterName());

        json.put("name", name);
        json.put("channelUserID", channelUserID);
        json.put("channelUserName", channelUserName);
        json.put("channelUserNick", channelUserNick);
        json.put("createTime", TimeUtils.format_default(createTime));
        json.put("lastLoginTime", TimeUtils.format_default(lastLoginTime));
        return json;
    }

    @JSONField(serialize=false)
    public UGame getGame(){

        return CacheManager.getInstance().getGame(this.appID);
    }

    @JSONField(serialize=false)
    public UChannel getChannel(){
        return CacheManager.getInstance().getChannel(this.channelID);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getAppID() {
        return appID;
    }

    public void setAppID(int appID) {
        this.appID = appID;
    }

    public int getChannelID() {
        return channelID;
    }

    public void setChannelID(int channelID) {
        this.channelID = channelID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChannelUserID() {
        return channelUserID;
    }

    public void setChannelUserID(String channelUserID) {
        this.channelUserID = channelUserID;
    }

    public String getChannelUserName() {
        return channelUserName;
    }

    public void setChannelUserName(String channelUserName) {
        this.channelUserName = channelUserName;
    }

    public String getChannelUserNick() {
        return channelUserNick;
    }

    public void setChannelUserNick(String channelUserNick) {
        this.channelUserNick = channelUserNick;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(Date lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public Integer getFirstCharge() {
        return firstCharge;
    }

    public void setFirstCharge(Integer firstCharge) {
        this.firstCharge = firstCharge;
    }

    public Date getFirstChargeTime() {
        return firstChargeTime;
    }

    public void setFirstChargeTime(Date firstChargeTime) {
        this.firstChargeTime = firstChargeTime;
    }

    public String getDeviceID() {
        return deviceID;
    }

    public void setDeviceID(String deviceID) {
        this.deviceID = deviceID;
    }

    public int getSubChannelID() {
        return subChannelID;
    }

    public void setSubChannelID(int subChannelID) {
        this.subChannelID = subChannelID;
    }

    public int getUniChannel() {
        return uniChannel;
    }

    public void setUniChannel(int uniChannel) {
        this.uniChannel = uniChannel;
    }
}
