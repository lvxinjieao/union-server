package com.u8.server.cache;

import com.u8.server.dao.hibernate.UChannelDao;
import com.u8.server.dao.hibernate.UChannelMasterDao;
import com.u8.server.dao.hibernate.UGameDao;
import com.u8.server.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * 在Spring启动的时候，加载需要缓存的数据
 */
@Repository
@Transactional(readOnly = true)
public class UApplicationListener implements ApplicationListener<ApplicationEvent> {

    private boolean loaded = false;

    @Autowired
    private UGameDao gameManager;

    @Autowired
    private UChannelMasterDao masterManager;

    @Autowired
    private UChannelDao channelManager;

    @Override
    public void onApplicationEvent(ApplicationEvent contextStartedEvent) {
        try{

            if(!loaded){

                Log.d("u8servermanager start...");

                CacheManager.getInstance().loadGameData(gameManager.findAll());

                CacheManager.getInstance().loadMasterData(masterManager.findAll());

                CacheManager.getInstance().loadChannelData(channelManager.findAll());

                loaded = true;
            }

        }catch (Exception e){
            Log.e("Load Data on server inited error.", e);
            Log.e(e.getMessage(), e);
        }
    }

}
