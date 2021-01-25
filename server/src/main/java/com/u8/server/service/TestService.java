package com.u8.server.service;

import com.u8.server.dao.cache.*;
import com.u8.server.data.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by ant on 2017/1/14.
 */
@Service("testService")
public class TestService {



    @Autowired
    private UGameCacheDao gameCacheDao;

    @Autowired
    private UChannelMasterCacheDao channelMasterCacheDao;

    @Autowired
    private UChannelCacheDao channelCacheDao;


    @Autowired
    private UUserCacheDao userCacheDao;

    @Autowired
    private UOrderCacheDao orderCacheDao;


    public UGame getGameFromRedis(int gameID){
        UGame game = gameCacheDao.get(gameID+"");
        return game;
    }

    public UChannelMaster getChannelMasterFromRedis(int masterID){
        UChannelMaster master = channelMasterCacheDao.get(masterID+"");
        return master;
    }

    public UChannel getChannelFromRedis(int channelID){
        UChannel channel = channelCacheDao.get(channelID+"");
        return channel;
    }

    public UUser getUserFromRedis(int userID){
        UUser user = userCacheDao.get(userID+"");
        return user;
    }

    public UOrder getOrderFromRedis(int orderID){
        UOrder order = orderCacheDao.get(orderID+"");
        return order;
    }


    public UUser generateUser(UChannel channel){
        UUser user = new UUser();
        user.setAppID(channel.getAppID());
        user.setChannelID(channel.getChannelID());
        user.setName(System.currentTimeMillis() + channel.getMaster().getNameSuffix());
        user.setChannelUserID(channel.getMaster().getSdkName()+"-"+System.currentTimeMillis());
        user.setChannelUserName("test-"+System.currentTimeMillis());
        user.setChannelUserNick("");
        Date now = new Date();
        user.setCreateTime(now);
        user.setLastLoginTime(now);
        user.setDeviceID("test");
        user.setFirstCharge(1);
        user.setFirstChargeTime(new Date());
        return user;
    }
}
