package com.u8.server.service;

import com.u8.server.common.UAnalyticsRedisTemplate;
import com.u8.server.constants.GlobalConstants;
import com.u8.server.dao.hibernate.UDeviceDao;
import com.u8.server.data.UDevice;
import com.u8.server.log.BLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ant on 2016/8/12.
 */
@Service("deviceManager")
public class UDeviceManager {

    @Autowired
    private UDeviceDao deviceDao;

    @Autowired
    private UAnalyticsRedisTemplate analyticsRedisTemplate;

    @Autowired
    private BLog log;

    public void saveDevice(UDevice device){
        deviceDao.save(device);
        log.printJson(BLog.T_DEVICE, BLog.OPTYPE.DEVICE.OP_ADD, device);

    }

    public UDevice getByDeviceID(String deviceID, Integer appID){
        String hql = "from UDevice where deviceID = ? and appID = ?";
        return (UDevice)deviceDao.findUnique(hql, deviceID, appID);
    }

    public void addDeviceCount(UDevice device){

        String gameKey = GlobalConstants.KEY.NEW_DEVICE_NUM_KEY + device.getAppID();
        String channelKey = GlobalConstants.KEY.NEW_DEVICE_NUM_KEY + device.getChannelID();

        analyticsRedisTemplate.incrementToday(gameKey, 1);
        analyticsRedisTemplate.incrementToday(channelKey, 1);

    }

}
