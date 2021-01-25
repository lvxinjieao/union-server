package com.u8.server.cache;

import com.u8.server.data.UChannel;
import com.u8.server.log.Log;
import com.u8.server.sdk.ISDKScript;
import com.u8.server.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 每个渠道SDK都有一个实现了ISDKScript接口的SDK逻辑处理类，登录认证和获取订单号接口中，通过反射的方式来
 * 实例化对应渠道的类，可能会导致一定的性能瓶颈。所以，这里我们增加一个缓存，第一次实例化之后，将对应渠道的处理类，缓存起来。
 * 后面使用的时候，直接从缓存中取
 * Created by xiaohei on 15/12/23.
 */
public class SDKCacheManager {

    private static SDKCacheManager instance;

    private Map<String, ISDKScript> cachedClasses;

    private SDKCacheManager() {
        cachedClasses = new ConcurrentHashMap<String, ISDKScript>();
    }

    public synchronized static SDKCacheManager getInstance() {
        if (instance == null) {
            instance = new SDKCacheManager();
        }
        return instance;
    }

    /***
     * 获取指定渠道的ISDKScript的实例的指定版本。
     *
     * 脚本类的版本区分，按照脚本类+版本数字命名规则。比如AnZhiSDK的2版本， 则在sdk目录下存在AnZhiSDK2.java.
     *
     * 版本号递增，从2开始。
     *
     * @param channel
     * @return
     */
    public ISDKScript getSDKScript(UChannel channel, String sdkVersionCode) {

        if (StringUtils.isEmpty(sdkVersionCode) || "1".equals(sdkVersionCode.trim())) {
            return findSDKScript(channel.getChannelVerifyClass());
        }

        String verifyClass = channel.getChannelVerifyClass() + sdkVersionCode;

        try {
            ISDKScript script = (ISDKScript) Class.forName(verifyClass).newInstance();
            cachedClasses.put(verifyClass, script);
            return script;
        } catch (InstantiationException e) {
            Log.e(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            Log.e(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            //Log.e(e.getMessage(), e);
            Log.e("classpath %s not exists. use default %s", verifyClass, channel.getChannelVerifyClass());
            return findSDKScript(channel.getChannelVerifyClass());
        }
        return null;
    }


    private synchronized ISDKScript findSDKScript(String classPath) {

        Log.d("sdk script path:%s", classPath);

        if (StringUtils.isEmpty(classPath)) {
            return null;
        }

        if (cachedClasses.containsKey(classPath.trim())) {
            return cachedClasses.get(classPath);
        }

        try {
            ISDKScript script = (ISDKScript) Class.forName(classPath).newInstance();
            cachedClasses.put(classPath, script);
            return script;
        } catch (InstantiationException e) {
            Log.e(e.getMessage(), e);
        } catch (IllegalAccessException e) {
            Log.e(e.getMessage(), e);
        } catch (ClassNotFoundException e) {
            Log.e(e.getMessage(), e);
        }

        return null;
    }


    public void destroy() {
        cachedClasses.clear();
    }

}
