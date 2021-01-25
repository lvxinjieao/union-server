package com.u8.server.dao.cache;

import com.u8.server.common.URedisTemplate;
import com.u8.server.data.UUser;
import com.u8.server.utils.TimeUtils;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

/**
 * 用户数据缓存操作接口
 * Created by ant on 2016/12/22.
 */
@Repository("userCacheDao")
public class UUserCacheDao extends URedisTemplate<UUser> {

    public static final String CP_HK = "uuser_cp_key";
    public static final String CP_UHK = "uuser_unic_key";

    @PostConstruct
    private void init(){
        //设置用户缓存数据，维持半个小时
        this.expire(getTypeKey(), 30, TimeUnit.MINUTES);
        this.expire(CP_HK, 30, TimeUnit.MINUTES);
        this.expire(CP_UHK, 30, TimeUnit.MINUTES);
    }

    @Override
    public String getTypeKey() {
        return "uuser";
    }

    @Override
    public String getDataKey(UUser data) {
        return data.getId()+"";
    }
}
