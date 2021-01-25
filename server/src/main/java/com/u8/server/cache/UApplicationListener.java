package com.u8.server.cache;

import com.u8.server.log.Log;

import com.u8.server.sdk.UHttpAgent;
import com.u8.server.sdk.ysdk.YSDKManager;
import com.u8.server.task.OrderTaskManager;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PreDestroy;

/**
 * 在Spring启动的时候，加载需要缓存的数据
 */
@Repository
@Transactional(readOnly = true)
public class UApplicationListener implements ApplicationListener<ApplicationEvent> {

    private boolean loaded = false;

    @Override
    public void onApplicationEvent(ApplicationEvent contextStartedEvent) {
        try {
            if (!loaded) {
                Log.d("u8server start...");
                loaded = true;
            }
        } catch (Exception e) {
            Log.e("Load Data on server inited error.", e);
            Log.e(e.getMessage(), e);
        }
    }

    @PreDestroy
    public void onDestory() {
        Log.d("Now to clean ...");
        YSDKManager.getInstance().destory();
        OrderTaskManager.getInstance().destory();
        UHttpAgent.getInstance().destroy();
        SDKCacheManager.getInstance().destroy();

    }
}
