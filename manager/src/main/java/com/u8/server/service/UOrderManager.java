package com.u8.server.service;

import com.u8.server.common.*;
import com.u8.server.constants.PayState;
import com.u8.server.dao.hibernate.UOrderDao;
import com.u8.server.data.UOrder;
import com.u8.server.log.BLog;
import com.u8.server.log.Log;
import com.u8.server.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service("orderManager")
public class UOrderManager {

    @Autowired
    private UOrderDao orderDao;

    public UOrder getOrder(long orderID) {

        return orderDao.get(orderID);
    }

    //根据渠道ID和渠道订单号查找订单信息
    public UOrder getOrderByChannelOrderID(int channelID, String channelOrderID) {
        String hql = "from UOrder where channelID = ? and channelOrderID = ?";
        return (UOrder) orderDao.findUnique(hql, channelID, channelOrderID);
    }

    //获取所有特定状态的订单
    public List<UOrder> getOrdersWithState(int state) {
        String hql = "from UOrder where state = ?";
        return orderDao.find(hql, new Object[]{state}, null);
    }

    public void saveOrder(UOrder order) {
        orderDao.save(order);
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

    public void deleteOrder(UOrder order) {
        orderDao.delete(order);
    }


    //分页查找
    public Page<UOrder> queryPage(int currPage, int num) {

        PageParameter page = new PageParameter(currPage, num, true);
        OrderParameters order = new OrderParameters();
        order.add("id", OrderParameter.OrderType.DESC);
        String hql = "from UOrder";
        return orderDao.find(page, hql, null, order);
    }

    //按照条件分页查找
    public Page<UOrder> queryPage(int currPage, int num, Long orderID, Integer appID, Integer channelID, Integer subChannelID,
                                  Integer userID, String username, String productID, String productName,
                                  Integer minMoney, Integer maxMoney, Integer minRealMoney, Integer maxRealMoney, String roleID, String roleName, String serverID,
                                  Integer state, String channelOrderID, String extension, Date beginCreateTime, Date endCreateTime, Object[] permisionedGameIDs) {


        SQLParams params = new SQLParams();

        if (orderID != null && orderID > 0) {
            params.EQ("orderID", orderID);
        }

        if (appID != null && appID > 0) {
            params.EQ("appID", appID);
        }
        if (channelID != null && channelID > 0) {
            params.EQ("channelID", channelID);
        }

        if (subChannelID != null && subChannelID > 0) {
            params.EQ("subChannelID", subChannelID);
        }

        if (userID != null && userID > 0) {
            params.EQ("userID", userID);
        }

        if (!StringUtils.isEmpty(username)) {
            params.EQ("username", username);
        }

        if (!StringUtils.isEmpty(productID)) {
            params.EQ("productID", productID);
        }

        if (!StringUtils.isEmpty(productName)) {
            params.EQ("productName", productName);
        }

        if (minMoney != null && minMoney > 0) {
            params.GE("money", minMoney);
        }

        if (maxMoney != null && maxMoney > 0) {
            params.LE("money", maxMoney);
        }

        if (minRealMoney != null && minRealMoney > 0) {
            params.GE("realMoney", minRealMoney);
        }

        if (maxRealMoney != null && maxRealMoney > 0) {
            params.LE("realMoney", maxRealMoney);
        }

        if (!StringUtils.isEmpty(roleID)) {
            params.EQ("roleID", roleID);
        }

        if (!StringUtils.isEmpty(roleName)) {
            params.EQ("roleName", roleName);
        }

        if (!StringUtils.isEmpty(serverID)) {
            params.EQ("serverID", serverID);
        }

        if (state != null && state >= 0) {
            params.EQ("state", state);
        }

        if (!StringUtils.isEmpty(channelOrderID)) {
            params.EQ("channelOrderID", channelOrderID);
        }

        if (!StringUtils.isEmpty(extension)) {
            params.EQ("extension", extension);
        }

        if (beginCreateTime != null) {
            params.GE("createdTime", beginCreateTime);
        }

        if (endCreateTime != null) {
            params.LE("createdTime", endCreateTime);
        }

        if (permisionedGameIDs != null) {
            if (permisionedGameIDs.length == 0) {
                permisionedGameIDs = new Object[]{0};
            }
            params.IN("appID", permisionedGameIDs);
        }


        PageParameter page = new PageParameter(currPage, num, true);
//        OrderParameters order = new OrderParameters();
//        order.add("id", OrderParameter.OrderType.DESC);
        String hql = "from UOrder" + params.getWhereSQL() + " order by orderID desc";

        Log.d("order search hql is ");
        Log.d(hql);

        return orderDao.find(page, hql, params.getWhereValues(), null);

    }
}
