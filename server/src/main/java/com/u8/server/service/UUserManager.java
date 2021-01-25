package com.u8.server.service;

import com.u8.server.common.UAnalyticsRedisTemplate;
import com.u8.server.config.GlobalConfig;
import com.u8.server.constants.GlobalConstants;
import com.u8.server.dao.cache.UUserCacheDao;
import com.u8.server.dao.hibernate.UUserDao;
import com.u8.server.data.UChannel;
import com.u8.server.data.UGame;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.SDKVerifyResult;
import com.u8.server.utils.EncryptUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("userManager")
public class UUserManager {

    @Autowired
    private UUserDao userDao;

    @Autowired
    private UUserCacheDao userCacheDao;

    @Autowired
    private GlobalConfig globalConfig;

    @Autowired
    private UAnalyticsRedisTemplate analyticsTemplate;

    //当日新增用户统计
    public void addNewUserCount(UUser user) {
        String gameKey = GlobalConstants.KEY.NEW_USER_NUM_KEY + user.getAppID();
        String channelKey = GlobalConstants.KEY.NEW_USER_NUM_KEY + user.getChannelID();
        analyticsTemplate.incrementToday(gameKey, 1);
        analyticsTemplate.incrementToday(channelKey, 1);
    }

    //当日活跃用户统计
    public void addUserCount(UUser user) {
        String gameKey = GlobalConstants.KEY.KEY_USER_LOGIN + user.getAppID();
        String channelKey = GlobalConstants.KEY.KEY_USER_LOGIN + user.getChannelID();
        analyticsTemplate.saddToday(gameKey, user.getId() + "");
        analyticsTemplate.saddToday(channelKey, user.getId() + "");
    }

    //根据渠道用户ID获取用户信息
    public UUser getUserByCpID(int appID, int channelID, String cpUserID) {
        String hql = "from UUser where appID = ? and channelID = ? and channelUserID = ?";
        String hqlKey = appID + channelID + cpUserID;
        UUser user = userCacheDao.get(UUserCacheDao.CP_HK, hqlKey);
        if (user == null) {
            user = (UUser) userDao.findUnique(hql, appID, channelID, cpUserID);
            if (user != null) {
                userCacheDao.save(UUserCacheDao.CP_HK, hqlKey, user);
            }
        }
        return user;
    }

    //根据互通渠道号获取用户信息
    public UUser getUserByUniChannel(int appID, int uniChannel, String cpUserID) {
        String hql = "from UUser where appID = ? and uniChannel = ? and channelUserID = ?";
        String hqlKey = appID + "" + uniChannel + cpUserID;
        UUser user = userCacheDao.get(UUserCacheDao.CP_UHK, hqlKey);
        if (user == null) {
            user = (UUser) userDao.findUnique(hql, appID, uniChannel, cpUserID);
            if (user != null) {
                userCacheDao.save(UUserCacheDao.CP_UHK, hqlKey, user);
            }
        }
        return user;
    }

    //测试代码使用
    public List<UUser> queryLastUsers(int max) {
        String hql = "from UUser order by id desc ";
        return userDao.findLimit(hql, new Object[]{}, null, max);
    }

    public UUser getUser(int userID) {
        UUser user = userCacheDao.get(userID + "");
        if (user == null) {
            user = userDao.get(userID);
            if (user != null) {
                userCacheDao.save(user);
            }
        }
        return user;
    }

    //校验sign
    public boolean isSignOK(String signStr, String sign) {
        String newSign = EncryptUtils.md5(signStr);
        Log.d("The newSign is " + newSign);
        return newSign.toLowerCase().equals(sign.toLowerCase());
    }

    public boolean checkUser(UUser user, String token) {
        long now = System.currentTimeMillis();
        if (!token.equals(user.getToken()) || (now - user.getLastLoginTime().getTime()) > 3600 * 1000) {
            return false;
        }
//        String tokenRight = UGenerator.generateToken(user, user.getGame().getAppSecret());
//        Log.d("The token is "+token +"; the right token is "+tokenRight);
        return true;
    }

    public UUser generateUser(UChannel channel, UGame game, Integer subChannelID, String nameSuffix, SDKVerifyResult cpUserInfo, String deviceID) {
        UUser user = new UUser();
        user.setAppID(game.getAppID());
        user.setChannelID(channel.getChannelID());
        user.setUniChannel(channel.getUniChannel() == null ? 0 : channel.getUniChannel());
        user.setSubChannelID(subChannelID == null ? 0 : subChannelID);
        user.setName(globalConfig.getDeployID() + "_" + System.nanoTime() + nameSuffix);
        user.setChannelUserID(cpUserInfo.getUserID());
        user.setChannelUserName(cpUserInfo.getUserName() == null ? "" : cpUserInfo.getUserName());
        user.setChannelUserNick(cpUserInfo.getNickName() == null ? "" : cpUserInfo.getNickName());
        Date now = new Date();
        user.setCreateTime(now);
        user.setLastLoginTime(now);
        user.setDeviceID(deviceID);
        userDao.save(user);             //这里保存一次， 回传userId， 后面生成token的时候， 会用到userId
        return user;
    }


    public void saveUser(UUser user) {
        boolean newUser = (user.getId() == null || user.getId() == 0);
        userDao.save(user);
        String hqlKey = user.getAppID() + user.getChannelID() + user.getChannelUserID();
        userCacheDao.save(UUserCacheDao.CP_HK, hqlKey, user);
        if (user.getUniChannel() != 0) {
            String uniKey = user.getAppID() + "" + user.getUniChannel() + user.getChannelUserID();
            userCacheDao.save(UUserCacheDao.CP_UHK, uniKey, user);
        }
        userCacheDao.save(user);
    }

}
