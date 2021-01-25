package com.u8.server.service;

import com.u8.server.cache.CacheManager;
import com.u8.server.common.Page;
import com.u8.server.dao.cache.UChannelMasterCacheDao;
import com.u8.server.dao.hibernate.UChannelMasterDao;
import com.u8.server.data.UChannelMaster;
import com.u8.server.log.BLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 渠道商管理
 */
@Service("channelMasterManager")
public class UChannelMasterManager {

    @Autowired
    private UChannelMasterDao channelMasterDao;

    @Autowired
    private UChannelMasterCacheDao channelMasterCacheDao;


    public void saveChannelMaster(final UChannelMaster master){

        if(master == null){
            return;
        }

        boolean isCreate = (master.getMasterID() == null);

        channelMasterCacheDao.doInLockWithRetry(master, new Runnable() {
            @Override
            public void run() {
                if(master.getMasterID() == null || master.getMasterID() == 0){
                    channelMasterDao.save(master);
                }else{
                    channelMasterDao.mergeSave(master);
                }
                CacheManager.getInstance().saveMaster(master);
                channelMasterCacheDao.save(master);

            }
        });

        BLog.getInstance().printJson(BLog.T_MASTER, isCreate ? BLog.OPTYPE.MASTER.OP_ADD : BLog.OPTYPE.MASTER.OP_UPDATE, master);

    }

    //删除渠道商
    public void deleteChannelMaster(UChannelMaster master){
        if(master == null){
            return;
        }

        channelMasterCacheDao.delete(master);
        channelMasterDao.delete(master);
        CacheManager.getInstance().removeMaster(master.getMasterID());


        BLog.getInstance().printJson(BLog.T_MASTER, BLog.OPTYPE.MASTER.OP_DELETE, master);
    }

    //根据ID获取渠道商数据
    public UChannelMaster getChannelMaster(int masterID){

        UChannelMaster master = CacheManager.getInstance().getMaster(masterID);
        if(master == null){
            master = channelMasterDao.get(masterID);
            if(master!= null){
                CacheManager.getInstance().saveMaster(master);
            }

        }
        return master;
    }


    //获取指定名称的渠道商数据(后台管理使用)
    public List<UChannelMaster> getMastersByName(String name){

        return channelMasterDao.getChannelMastersByName(name);
    }

    //打包工具使用
    public UChannelMaster getMasterBySDKName(String sdkName){
        return channelMasterDao.getChannelMasterBySDKName(sdkName);
    }


    //获取所有渠道商信息(后台管理使用)
    public List<UChannelMaster> queryAll(){

        return channelMasterDao.findAll();
    }


    //分页查找，直接从数据库查找(后台管理使用)
    public Page<UChannelMaster> queryPage(int currPage, int num, Integer masterID, String sdkName, String masterName, String nameSuffix,
                                          String verifyClass) {

        return channelMasterDao.queryPage(currPage, num, masterID, sdkName, masterName, nameSuffix, verifyClass);

    }

}
