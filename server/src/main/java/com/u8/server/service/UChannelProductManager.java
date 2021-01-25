package com.u8.server.service;

import com.u8.server.common.Page;
import com.u8.server.dao.cache.UChannelProductCacheDao;
import com.u8.server.dao.hibernate.UChannelProductDao;
import com.u8.server.data.UChannelProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ant on 2018/5/15.
 */
@Service("channelProductManager")
public class UChannelProductManager {

    @Autowired
    private UChannelProductDao channelProductDao;

    @Autowired
    private UChannelProductCacheDao channelProductCacheDao;

    //如果有渠道商品映射关系配置，那么获取渠道那边的商品ID;没有的话，直接返回productID
    public String getChannelProductID(int channelID, String productID){

        String channelProductID = productID;
        UChannelProduct product = getChannelProduct(channelID, productID);
        if(product != null){
            channelProductID = product.getChannelProductID();
        }

        return channelProductID;
    }

    private void saveChannelProductInCache(UChannelProduct product){

        channelProductCacheDao.saveInLock(getLockKey(product), product);
    }

    private UChannelProduct getChannelProductInCache(int channelID, String gameProductID){
        return channelProductCacheDao.get("channel"+channelID, gameProductID);
    }


    public UChannelProduct getChannelProduct(int channelID, String gameProductID){
        UChannelProduct product = getChannelProductInCache(channelID, gameProductID);
        if(product == null){
            product = channelProductDao.getChannelProduct(channelID, gameProductID);
            if(product != null){
                saveChannelProductInCache(product);
            }
        }
        return product;

    }

    private String getLockKey(UChannelProduct product){
        String lockKey = "channel"+product.getChannelID()+product.getGameProductID();
        return lockKey;
    }
}
