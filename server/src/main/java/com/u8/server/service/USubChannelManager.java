package com.u8.server.service;

import com.u8.server.dao.cache.USubChannelCacheDao;
import com.u8.server.dao.hibernate.USubChannelDao;
import com.u8.server.data.USubChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 子渠道管理
 * Created by ant on 2017/2/7.
 */
@Service("subChannelManager")
public class USubChannelManager {

    @Autowired
    private USubChannelDao subChannelDao;

    @Autowired
    private USubChannelCacheDao subChannelCacheDao;


    //子渠道是否存在
    public boolean isSubChannelValid(int channelID, int subChannelID){

        return getSubChannel(channelID, subChannelID) != null;
    }

    //获取子渠道信息
    public USubChannel getSubChannel(int channelID, int subChannelID){

        if(subChannelID <= 0){
            return null;
        }

        USubChannel subChannel = subChannelCacheDao.getSubChannel(channelID, subChannelID);
        if(subChannel == null){
            String hql = "from USubChannel where channelID = ? and subChannelID = ?";
            subChannel = (USubChannel) subChannelDao.findUnique(hql, channelID, subChannelID);
            if (subChannel != null) {
                subChannelCacheDao.saveInLock(subChannel);
            }
        }

        return subChannel;
    }

}
