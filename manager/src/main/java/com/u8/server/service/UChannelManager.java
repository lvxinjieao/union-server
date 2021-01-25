package com.u8.server.service;

import com.u8.server.cache.CacheManager;
import com.u8.server.common.Page;
import com.u8.server.dao.cache.UChannelCacheDao;
import com.u8.server.dao.hibernate.UChannelDao;
import com.u8.server.dao.hibernate.UChannelParamMetaDao;
import com.u8.server.data.UChannel;
import com.u8.server.data.UChannelParamMeta;
import com.u8.server.log.BLog;
import com.u8.server.log.Log;
import com.u8.server.utils.StringUtils;
import net.sf.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;
import java.util.List;

/**
 */
@Service("channelManager")
public class UChannelManager {

    @Autowired
    private UChannelDao channelDao;

    @Autowired
    private UChannelCacheDao channelCacheDao;


    @Autowired
    private UChannelParamMetaDao channelParamMetaDao;

    //获取对应的渠道参数配置项
    public List<UChannelParamMeta> getParamMetasByChannel(UChannel channel){

        List<UChannelParamMeta> metas = channelParamMetaDao.getMetaListByMaster(channel.getMasterID());
        //List<UChannelParam> params = channelParamDao.getParamsByMasterID(masterID);

        if(metas == null || metas.size() == 0){
            Log.e("masterID:%s has no meta info.", channel.getMasterID());
            return null;
        }

        if(!StringUtils.isEmpty(channel.getChannelParams())){
            try{

                JSONObject json = JSONObject.fromObject(channel.getChannelParams());
                Iterator iterator = json.keys();
                while(iterator.hasNext()){
                    String key = (String)iterator.next();

                    for(UChannelParamMeta meta : metas){
                        if(meta.getParamKey() != null && meta.getParamKey().equals(key)){
                            String val = (String)json.optString(key);
                            meta.setDefaultVal(val);
                            break;
                        }
                    }

                }

            }catch (Exception e){
                Log.e(e.getMessage(),e);
                e.printStackTrace();
            }
        }

        return metas;

    }


    //获取当前一个可用的渠道号，默认算法是获取一个当前最大渠道号+1
    public int getValidChannelID(){

        List<UChannel> lst = channelDao.findAll();

        int max = 0;

        for(UChannel c : lst){
            if(c.getChannelID() > max){
                max = c.getChannelID();
            }
        }

        return max+1;
    }

    //获取指定游戏的渠道数量(后台管理调用)
    public long getChannelCountByGame(int appID){

        return channelDao.getChannelCountByGame(appID);

    }

    public List<UChannel> getAllChannelsByAppID(int appID){

        return channelDao.getChannelsByAppID(appID);
    }

    //获取指定渠道商下的渠道数量(后台管理调用)
    public long getChannelCountByMaster(int masterID){

        return channelDao.getChannelCountByMaster(masterID);

    }

    //获取所有渠道数据（测试使用）
    public List<UChannel> getAll(){

        return channelDao.findAll();
    }

    //分页查找，直接从数据库查找(后台管理调用)
    public Page<UChannel> queryPage(int currPage, int num, Integer channelID, Integer appID, Object[] masterIDs, Object[] permissionedGameIDs) {

        return channelDao.queryPage(currPage, num, channelID, appID, masterIDs, permissionedGameIDs);

    }


    //添加或者修改channel
    public void saveChannel(final UChannel channel){

        boolean isCreate = (channel.getId() == null);

        if(channel.getChannelID() <= 0){
            channel.setChannelID(getValidChannelID());
        }

        if(channel.getUniChannel() == null){
            channel.setUniChannel(0);
        }

        channelCacheDao.doInLockWithRetry(channel, new Runnable() {
            @Override
            public void run() {
                channelDao.save(channel);

                if(channel.getId() == null || channel.getId() == 0){
                    channelDao.save(channel);
                }else{
                    channelDao.mergeSave(channel);
                }
                CacheManager.getInstance().saveChannel(channel);
                channelCacheDao.save(channel);

            }
        });


        BLog.getInstance().printJson(BLog.T_CHANNEL, isCreate ? BLog.OPTYPE.CHANNEL.OP_ADD : BLog.OPTYPE.CHANNEL.OP_UPDATE, channel);

    }

    public UChannel getChannelByID(int id){

        return channelDao.get(id);
    }

    public UChannel getChannel(int channelID){

        UChannel channel = CacheManager.getInstance().getChannel(channelID);
        if(channel == null){
            channel = channelDao.getByChannelID(channelID);
            if(channel != null){
                CacheManager.getInstance().saveChannel(channel);
            }

        }

        return channel;
    }

    public void deleteChannelFromCache(UChannel channel){

        channelCacheDao.delete(channel);
    }

    public void deleteChannel(UChannel channel){
        if(channel == null){
            return;
        }
        channelDao.delete(channel);
        CacheManager.getInstance().removeChannel(channel.getChannelID());
        channelCacheDao.delete(channel);

        BLog.getInstance().printJson(BLog.T_CHANNEL, BLog.OPTYPE.CHANNEL.OP_DELETE, channel);

    }


}
