package com.u8.server.dao.cache;

import com.u8.server.common.URedisTemplate;
import com.u8.server.data.UChannel;
import org.springframework.stereotype.Repository;

/**
 * 渠道数据缓存操作接口
 * Created by ant on 2016/12/19.
 */
@Repository("channelCacheDao")
public class UChannelCacheDao extends URedisTemplate<UChannel>{


    @Override
    public String getTypeKey() {
        return "uchannel";
    }

    @Override
    public String getDataKey(UChannel data) {
        return data.getChannelID()+"";
    }
}
