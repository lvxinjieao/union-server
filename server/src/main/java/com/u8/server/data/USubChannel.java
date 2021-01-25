package com.u8.server.data;

import com.alibaba.fastjson.annotation.JSONField;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 子渠道
 * Created by ant on 2017/2/7.
 */
@Entity
@Table(name = "usubchannel")
public class USubChannel implements Serializable {

    private static final long serialVersionUID = 1120411125784984491L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;                 //数据表中ID

    private int subChannelID;           //子渠道号
    private String subChannelName;      //子渠道名称

    private int channelID;              //所属渠道ID
    private int appID;                  //游戏ID
    private int masterID;               //渠道商ID

    private Integer openPayFlag;            //充值功能状态，0：开放；1：关闭。关闭之后，该渠道获取订单号会返回一个错误

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

    public int getSubChannelID() {
        return subChannelID;
    }

    public void setSubChannelID(int subChannelID) {
        this.subChannelID = subChannelID;
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

    public Integer getOpenPayFlag() {
        return openPayFlag;
    }

    public void setOpenPayFlag(Integer openPayFlag) {
        this.openPayFlag = openPayFlag;
    }

    public String getSubChannelName() {
        return subChannelName;
    }

    public void setSubChannelName(String subChannelName) {
        this.subChannelName = subChannelName;
    }
}
