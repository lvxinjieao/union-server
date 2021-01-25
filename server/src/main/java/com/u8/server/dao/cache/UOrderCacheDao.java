package com.u8.server.dao.cache;

import com.u8.server.common.URedisTemplate;
import com.u8.server.data.UOrder;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 订单数据缓存操作接口
 * Created by ant on 2016/12/22.
 */
@Repository("orderCacheDao")
public class UOrderCacheDao extends URedisTemplate<UOrder>{


    @PostConstruct
    private void init(){
        //设置订单缓存数据，维持半个小时
        this.expire(getTypeKey(), 30, TimeUnit.MINUTES);
    }

    @Override
    public String getTypeKey() {
        return "uorder";
    }

    @Override
    public String getDataKey(UOrder data) {
        return data.getOrderID()+"";
    }
}
