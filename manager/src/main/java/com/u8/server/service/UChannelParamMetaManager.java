package com.u8.server.service;

import com.u8.server.common.Page;
import com.u8.server.dao.hibernate.UChannelParamMetaDao;
import com.u8.server.data.UChannelParamMeta;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by ant on 2018/2/22.
 */
@Service("channelParamMetaManager")
public class UChannelParamMetaManager {

    @Autowired
    private UChannelParamMetaDao channelParamMetaDao;


    public Page<UChannelParamMeta> queryPage(int currPage, int num, Integer masterID) {

        return channelParamMetaDao.queryPage(currPage, num, masterID);
    }

    public List<UChannelParamMeta> getMetaListByMaster(Integer masterID){

        return channelParamMetaDao.getMetaListByMaster(masterID);
    }

    public UChannelParamMeta getMetaByID(Integer id){
        return channelParamMetaDao.get(id);
    }

    public UChannelParamMeta getMetaByKey(Integer masterID, String key){
        return channelParamMetaDao.getMetaByKey(masterID, key);
    }

    public void saveMeta(UChannelParamMeta meta){
        if(meta == null)return;
        channelParamMetaDao.save(meta);
    }

    public void deleteMeta(UChannelParamMeta meta){
        if(meta == null)return;
        channelParamMetaDao.delete(meta);
    }

}
