package com.u8.server.dao.cache;

import com.u8.server.common.URedisTemplate;
import com.u8.server.data.UChannelMaster;
import org.springframework.stereotype.Repository;

/**
 * 渠道商数据缓存操作接口
 * Created by ant on 2016/12/19.
 */
@Repository("channelMasterCacheDao")
public class UChannelMasterCacheDao extends URedisTemplate<UChannelMaster>{
    @Override
    public String getTypeKey() {
        return "uchannelmaster";
    }

    @Override
    public String getDataKey(UChannelMaster data) {
        return data.getMasterID()+"";
    }
}
