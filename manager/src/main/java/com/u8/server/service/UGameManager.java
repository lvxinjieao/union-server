package com.u8.server.service;

import com.u8.server.cache.CacheManager;
import com.u8.server.common.Page;
import com.u8.server.dao.cache.UGameCacheDao;
import com.u8.server.dao.hibernate.UGameDao;
import com.u8.server.data.UGame;
import com.u8.server.log.BLog;
import com.u8.server.utils.RSAUtils;
import com.u8.server.utils.UGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service("gameManager")
public class UGameManager {

    @Autowired
    private UGameDao gameDao;

    @Autowired
    private UGameCacheDao gameCacheDao;

    /**
     * 后台管理中生成一条新的游戏记录
     * @param name
     * @param payCallback
     * @param payCallbackDebug
     * @param msdkCallback
     * @return
     */
    public UGame generateGame(String name, String payCallback, String payCallbackDebug, String msdkCallback, Integer cpID, Integer isSingle){

        UGame game = new UGame();
        //int appID = IDGenerator.getInstance().nextAppID();
        //long currTime = System.currentTimeMillis();
        //game.setAppID(appID);
        game.setAppkey(UGenerator.generateAppKey());
        game.setAppSecret(UGenerator.generateAppSecret());
        game.setName(name);
        game.setPayCallback(payCallback);
        game.setCpID(cpID);
        game.setIsSingle(isSingle);

        try {
            Map<String, Object> keys = RSAUtils.generateKeys();
            game.setAppRSAPubKey(RSAUtils.getPublicKey(keys));
            game.setAppRSAPriKey(RSAUtils.getPrivateKey(keys));
        } catch (Exception e) {
            e.printStackTrace();
        }

        saveGame(game);

        return game;
    }


    /**
     * 存储的时候，同时将该条游戏记录同步到缓存中
     * @param game
     */
    public void saveGame(final UGame game){


        gameCacheDao.doInLockWithRetry(game, new Runnable() {
            @Override
            public void run() {
                if(game.getAppID() == null || game.getAppID() == 0){
                    gameDao.save(game);
                }else{
                    gameDao.mergeSave(game);
                }
                CacheManager.getInstance().saveGame(game);
                gameCacheDao.save(game);

            }
        });


        boolean isCreate = (game.getAppID() == null);


        BLog.getInstance().printJson(BLog.T_GAME, isCreate ? BLog.OPTYPE.GAME.OP_ADD : BLog.OPTYPE.GAME.OP_UPDATE, game);
    }

    /**
     * 删除的时候，同时将该条记录从缓存中删除
     * @param game
     */
    public void deleteGame(UGame game){

        if(game == null){
            return;
        }

        CacheManager.getInstance().removeGame(game.getAppID());
        gameCacheDao.delete(game);
        gameDao.delete(game);



        BLog.getInstance().printJson(BLog.T_GAME, BLog.OPTYPE.GAME.OP_DELETE, game);

    }

    /**
     * 查找的时候，先从缓存中查找，缓存中没有的话，加入到缓存中
     * @param appID
     * @return
     */
    public UGame getGame(int appID){

        UGame game = CacheManager.getInstance().getGame(appID);

        if(game == null){
            game = gameDao.get(appID);
            if(game != null){
                CacheManager.getInstance().saveGame(game);
            }

        }

        return game;
    }

    //获取指定名称的游戏(后台管理使用)
    public List<UGame> getGameByName(String name){

        return gameDao.getGamesByName(name);
    }

    //查找多个ugame(后台管理使用)
    public List<UGame> queryAllGames(Object[] permissionedGameIDs){
        return gameDao.getGamesByAppIds(permissionedGameIDs);
    }

    //分页查找， 直接从数据库中查找(后台管理使用)
    public Page<UGame> queryPage(int currPage, int num, Integer appID, String appKey, String appSecret, String name, Object[] permissionedGameIDs){

        return gameDao.queryPage(currPage, num, appID, appKey, appSecret, name, permissionedGameIDs);
    }

    //查询不在appIDs中的游戏
    public List<UGame> queryGamesExclude(Object[] appIDs){
        return gameDao.getGamesExclude(appIDs);
    }
}
