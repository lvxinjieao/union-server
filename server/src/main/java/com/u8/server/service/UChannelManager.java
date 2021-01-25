package com.u8.server.service;

import com.u8.server.cache.CacheManager;
import com.u8.server.common.Page;
import com.u8.server.common.PageParameter;
import com.u8.server.common.SQLParams;
import com.u8.server.dao.cache.UChannelCacheDao;
import com.u8.server.dao.cache.UChannelMasterCacheDao;
import com.u8.server.dao.cache.UUserCacheDao;
import com.u8.server.dao.hibernate.UChannelDao;
import com.u8.server.data.UChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 */
@Service("channelManager")
public class UChannelManager {

    @Autowired
    private UChannelDao channelDao;

    @Autowired
    private UChannelCacheDao channelCacheDao;

    //获取所有渠道数据（测试使用）
    public List<UChannel> getAll() {
        return channelDao.findAll();
    }

    //获取指定渠道号的渠道数据
    public UChannel getChannel(int id) {
        UChannel channel = channelCacheDao.get(id + "");
        if (channel == null) {
            channel = channelDao.getByChannelID(id);
            if (channel != null) {
                channelCacheDao.saveInLock(channel);
            }
        }
        return channel;
    }


}
