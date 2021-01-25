package com.u8.server.dao.cache;

import com.u8.server.common.URedisTemplate;
import com.u8.server.data.UChannelProduct;
import org.springframework.stereotype.Repository;

/**
 * Created by ant on 2018/5/15.
 */
@Repository("channelProductCacheDao")
public class UChannelProductCacheDao extends URedisTemplate<UChannelProduct> {
    @Override
    public String getTypeKey() {
        return "uchannelproduct";
    }

    @Override
    public String getDataKey(UChannelProduct data) {
        return data.getId()+"";
    }
}
