package com.u8.server.service;

import com.u8.server.common.*;
import com.u8.server.constants.GlobalConstants;
import com.u8.server.constants.PayState;
import com.u8.server.dao.cache.UOrderCacheDao;
import com.u8.server.dao.hibernate.UOrderDao;
import com.u8.server.data.UGame;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.BLog;
import com.u8.server.log.Log;
import com.u8.server.utils.IDGenerator;
import com.u8.server.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("orderManager")
public class UOrderManager {

    private static final String CP_HK = "uorder_cp_key";

    @Autowired
    private UOrderDao orderDao;

    @Autowired
    private UOrderCacheDao orderCacheDao;

    @Autowired
    private UAnalyticsRedisTemplate analyticsRedisTemplate;

    public UOrder getOrder(long orderID) {
        UOrder order = orderCacheDao.get(orderID + "");
        if (order == null) {

            order = orderDao.get(orderID);
            if (order != null)
                orderCacheDao.save(order);
        }

        return order;

    }

    //统计累计充值金额
    public void addMoneySum(UOrder order) {
        String gameKey = GlobalConstants.KEY.PAY_MONEY_COUNT + order.getAppID();
        String channelKey = GlobalConstants.KEY.PAY_MONEY_COUNT + order.getChannelID();
        analyticsRedisTemplate.incrementToday(gameKey, order.getMoney());
        analyticsRedisTemplate.incrementToday(channelKey, order.getMoney());
    }

    //统计充值用户
    public void addPayedUser(UOrder order) {
        String gameKey = GlobalConstants.KEY.KEY_USER_PAYED + order.getAppID();
        String channelKey = GlobalConstants.KEY.KEY_USER_PAYED + order.getChannelID();
        analyticsRedisTemplate.saddToday(gameKey, order.getUserID() + "");
        analyticsRedisTemplate.saddToday(channelKey, order.getUserID() + "");
    }

    //根据渠道ID和渠道订单号查找订单信息
    public UOrder getOrderByChannelOrderID(int channelID, String channelOrderID) {
        String hql = "from UOrder where channelID = ? and channelOrderID = ?";
        return (UOrder) orderDao.findUnique(hql, channelID, channelOrderID);
    }

    public void saveOrder(UOrder order) {
        orderDao.save(order);
        orderCacheDao.save(order);

        switch (order.getState()) {
            case PayState.STATE_PAYING:
                BLog.getInstance().printJson(BLog.T_ORDER, BLog.OPTYPE.ORDER.OP_CREATE, order);
                break;
            case PayState.STATE_SUC:
                BLog.getInstance().printJson(BLog.T_ORDER, BLog.OPTYPE.ORDER.OP_SUCCESS, order);
                break;
            case PayState.STATE_COMPLETE:
                BLog.getInstance().printJson(BLog.T_ORDER, BLog.OPTYPE.ORDER.OP_COMPLETE, order);
                break;
        }

    }

    public void insertOrder(UOrder order) {
        orderDao.insert(order);
        orderCacheDao.save(order);

        switch (order.getState()) {
            case PayState.STATE_PAYING:
                BLog.getInstance().printJson(BLog.T_ORDER, BLog.OPTYPE.ORDER.OP_CREATE, order);
                break;
            case PayState.STATE_SUC:
                BLog.getInstance().printJson(BLog.T_ORDER, BLog.OPTYPE.ORDER.OP_SUCCESS, order);
                break;
            case PayState.STATE_COMPLETE:
                BLog.getInstance().printJson(BLog.T_ORDER, BLog.OPTYPE.ORDER.OP_COMPLETE, order);
                break;
        }

    }


    public UOrder generateOrder(int userID, String userName, int appID, int channelID, int subChannelID, long orderID, int money, String productID, String productName, String productDesc, String roleID, String roleName, String roleLevel, String serverID, String serverName, String extension, String notifyUrl) {

        UOrder order = new UOrder();
        order.setOrderID(orderID);
        order.setAppID(appID);
        order.setChannelID(channelID);
        order.setSubChannelID(subChannelID);
        order.setMoney(money);
        order.setProductID(productID);
        order.setProductName(productName);
        order.setProductDesc(productDesc);
        order.setCurrency("RMB");
        order.setUserID(userID);
        order.setUsername(userName);
        order.setExtension(extension);
        order.setState(PayState.STATE_PAYING);
        order.setChannelOrderID("");
        order.setRoleID(roleID);
        order.setRoleName(roleName);
        order.setRoleLevel(roleLevel);
        order.setServerID(serverID);
        order.setServerName(serverName);
        order.setCreatedTime(new Date());
        order.setNotifyUrl(notifyUrl);

        insertOrder(order);

        return order;
    }
}
