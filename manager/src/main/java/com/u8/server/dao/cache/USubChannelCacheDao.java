package com.u8.server.dao.cache;

import com.u8.server.common.URedisTemplate;
import com.u8.server.data.USubChannel;
import org.springframework.stereotype.Repository;

/**
 * 子渠道缓存数据访问接口
 * Created by ant on 2017/2/7.
 */
@Repository("subChannelCacheDao")
public class USubChannelCacheDao extends URedisTemplate<USubChannel> {

    public USubChannel getSubChannel(int channelID, int subChannelID){

        return get(channelID+""+subChannelID);
    }

    @Override
    public String getTypeKey() {
        return "subchannel";
    }

    @Override
    public String getDataKey(USubChannel data) {
        return data.getChannelID()+""+data.getSubChannelID();
    }
}
