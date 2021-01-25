package com.u8.server.service;

import com.u8.server.common.Page;
import com.u8.server.dao.cache.UGameCacheDao;
import com.u8.server.dao.hibernate.UGameDao;
import com.u8.server.data.*;
import com.u8.server.utils.IDGenerator;
import com.u8.server.utils.RSAUtils;
import com.u8.server.utils.UGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;


@Service("gameManager")
public class UGameManager {

    @Autowired
    private UGameDao gameDao;

    @Autowired
    private UGameCacheDao gameCacheDao;

    /**
     * 查找的时候，先从缓存中查找，缓存中没有的话，加入到缓存中
     * @param appID
     * @return
     */
    public UGame getGame(int appID){

        UGame game = gameCacheDao.get(appID+"");
        if(game == null){
            game = gameDao.get(appID);
            if(game != null){
                gameCacheDao.saveInLock(game);
            }
        }
        return game;
    }
}
