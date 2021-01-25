package com.u8.server.service;

import com.u8.server.cache.CacheManager;
import com.u8.server.common.Page;
import com.u8.server.common.PageParameter;
import com.u8.server.common.SQLParams;
import com.u8.server.dao.cache.UChannelMasterCacheDao;
import com.u8.server.dao.hibernate.UChannelMasterDao;
import com.u8.server.data.UChannel;
import com.u8.server.data.UChannelMaster;
import com.u8.server.utils.IDGenerator;
import com.u8.server.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 渠道商管理
 */
@Service("channelMasterManager")
public class UChannelMasterManager {

    @Autowired
    private UChannelMasterDao channelMasterDao;

    @Autowired
    private UChannelMasterCacheDao channelMasterCacheDao;


    //根据ID获取渠道商数据
    public UChannelMaster getChannelMaster(int masterID){

        UChannelMaster master = channelMasterCacheDao.get(masterID+"");
        if(master == null){
            master = channelMasterDao.get(masterID);
            if(master!= null){
                channelMasterCacheDao.saveInLock(master);
            }

        }
        return master;
    }


}
