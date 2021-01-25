package com.u8.server.web.test;

import com.u8.server.cache.UApplicationContext;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.UHttpAgent;
import com.u8.server.service.TestService;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.UOrderManager;
import com.u8.server.service.UUserManager;
import com.u8.server.utils.IDGenerator;
import com.u8.server.web.pay.SendAgent;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by ant on 2017/1/14.
 */
@Controller
@Namespace("/user")
public class TestAction extends UActionSupport{

    private Integer channelID;
    private Integer userID;
    private Long orderID;

    @Autowired
    private TestService testService;

    @Autowired
    private UChannelManager channelManager;

    @Autowired
    private UUserManager userManager;

    @Autowired
    private UOrderManager orderManager;

    @Action("testAction")
    public void testAction(){
        UChannel channel = channelManager.getChannel(10);
        Log.e("verifyUrl:"+channel.getChannelAuthUrl());
    }

    @Action("testChannelGet")
    public void channelGet(){
        long time = System.currentTimeMillis();
        UChannel channel = channelManager.getChannel(channelID);
        if(channel == null){
            Log.e("Channel get failed. channelID:%s", channelID);

        }else{
            Log.d("Channel get success. channelID:%s, time:%s", channelID, (System.currentTimeMillis() - time));
        }
    }

    @Action("testChannelGetCache")
    public void channelGetFromCache(){
        long time = System.currentTimeMillis();
        UChannel channel = testService.getChannelFromRedis(channelID);
        if(channel == null){
            Log.e("Channel get from cache failed. channelID:%s", channelID);

        }else{
            Log.d("Channel get from cache success. channelID:%s, time:%s", channelID, (System.currentTimeMillis() - time));
        }
    }

    @Action("testUserAdd")
    public void userAdd(){
        UChannel channel = channelManager.getChannel(40);
        Log.d("get channel normal...");
        UUser user = testService.generateUser(channel);
        Log.d("generate a new user...");
        userManager.saveUser(user);
        Log.d("save user to mysql and redis..."+user.getId());
    }

    @Action("testUserGetCache")
    public void userGetFromCache(){

        TestService service = (TestService)UApplicationContext.getBean("testService");
        long time = System.currentTimeMillis();
        UUser user = service.getUserFromRedis(this.userID);
        if(user == null){
            Log.e("User get from cache failed. userID:%s", this.userID);
        }else{
            Log.d("User get from cache success. userID:%s;time:%s", this.userID, (System.currentTimeMillis()-time));
        }
    }

    @Action("testNotify")
    public void testNotify(){

        UOrder order = orderManager.getOrder(orderID);
        if(order == null){
            renderText("order not exists");
            return;
        }

        SendAgent.resendCallbackToServer(orderManager, order);

    }

    @Action("testUserGet")
    public void userGet(){

        UUserManager manager = (UUserManager)UApplicationContext.getBean("userManager");
        long time = System.currentTimeMillis();
        UUser user = manager.getUser(this.userID);
        if(user == null){
            Log.e("User get failed. userID:%s", this.userID);
        }else{
            Log.d("User get success. userID:%s,time:%s", this.userID, (System.currentTimeMillis() - time));
        }
    }

    public static void main(String[] args){

    }

    public Integer getChannelID() {
        return channelID;
    }

    public void setChannelID(Integer channelID) {
        this.channelID = channelID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }
}
