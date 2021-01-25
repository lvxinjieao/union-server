package com.u8.server.dao.cache;


import com.u8.server.common.URedisTemplate;
import com.u8.server.data.UGame;
import org.springframework.stereotype.Repository;

/**
 * 游戏数据缓存操作接口
 * Created by ant on 2016/12/18.
 */
@Repository("gameCacheDao")
public class UGameCacheDao extends URedisTemplate<UGame>{



    @Override
    public String getTypeKey() {
        return "ugame";
    }

    @Override
    public String getDataKey(UGame data) {
        return data.getAppID()+"";
    }
}
