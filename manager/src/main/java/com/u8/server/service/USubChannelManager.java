package com.u8.server.service;

import com.u8.server.common.Page;
import com.u8.server.dao.cache.USubChannelCacheDao;
import com.u8.server.dao.hibernate.USubChannelDao;
import com.u8.server.data.USubChannel;
import com.u8.server.log.BLog;
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


    public void saveSubChannel(final USubChannel channel){
        if(channel == null) return;

        subChannelCacheDao.doInLockWithRetry(channel, new Runnable() {
            @Override
            public void run() {
                subChannelDao.save(channel);
                subChannelCacheDao.save(channel);
            }
        });


        boolean isCreate = (channel.getId() == null);
        BLog.getInstance().printJson(BLog.T_SUBCHANNEL, isCreate ? BLog.OPTYPE.SUBCHANNEL.OP_ADD : BLog.OPTYPE.SUBCHANNEL.OP_UPDATE, channel);
    }

    public void deleteSubChannel(final USubChannel channel){
        if(channel == null){
            return;
        }

        subChannelCacheDao.doInLockWithRetry(channel, new Runnable() {
            @Override
            public void run() {
                subChannelCacheDao.delete(channel);
                subChannelDao.delete(channel);
            }
        });


        BLog.getInstance().printJson(BLog.T_SUBCHANNEL, BLog.OPTYPE.SUBCHANNEL.OP_DELETE, channel);
    }

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

    //分页查找，直接从数据库查找(后台管理调用)
    public Page<USubChannel> queryPage(int currPage, int num, Integer channelID, Integer appID, Integer subChannelID, Object[] permissionedGameIDs) {

        return subChannelDao.queryPage(currPage, num, channelID, appID, subChannelID, permissionedGameIDs);

    }

}
