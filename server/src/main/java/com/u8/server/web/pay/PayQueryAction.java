package com.u8.server.web.pay;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UGame;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * 单机游戏， 支付完成到server端来检查是否已经收到渠道方的支付回调通知。
 */
@Controller
@Namespace("/pay")
public class PayQueryAction extends UActionSupport {

    private Integer appID;
    private Integer channelID;
    private String orderID;
    private String t;
    private String sign;

    @Autowired
    private UChannelManager channelManager;

    @Autowired
    private UOrderManager orderManager;


    @Action("check")
    public void payQuery() {
        try{

            UChannel channel = channelManager.getChannel(this.channelID);
            if(channel == null){
                Log.e("pay query failed. channel not exists. channelID:"+this.channelID);
                renderState(false, 0);
                return;
            }

            UOrder order = orderManager.getOrder(Long.valueOf(orderID));

            if(order == null){
                Log.e("pay query failed. the order is null or the channel is null.orderID:%s", orderID);
                this.renderState(false, 0);
                return;
            }

            UGame game = channel.getGame();

            StringBuilder sb = new StringBuilder();
            sb.append("appID=").append(game.getAppID())
                    .append("channelID=").append(channel.getChannelID())
                    .append("orderID=").append(order.getOrderID())
                    .append("t=").append(t)
                    .append(game.getAppkey());

            String md5 = EncryptUtils.md5(sb.toString()).toLowerCase();

            Log.d("payQuery: sign str:%s; sign local:%s; sign:%s", sb.toString(), md5, sign);

            if(!md5.equals(sign)){
                Log.e("payQuery: sign not match.");
                this.renderState(false, 0);
                return;
            }

            if(order.getState() >= PayState.STATE_SUC){
                renderState(true, 1);
            }else{
                this.renderState(true, 0);
            }

        }catch (Exception e){
            this.renderState(false, 0);
            e.printStackTrace();
        }

    }

    private void renderState(boolean suc, int state){
        JSONObject json = new JSONObject();
        json.put("suc", suc ? 1 : 0);
        json.put("state", state);
        renderJson(json.toString());
    }

    public Integer getAppID() {
        return appID;
    }

    public void setAppID(Integer appID) {
        this.appID = appID;
    }

    public Integer getChannelID() {
        return channelID;
    }

    public void setChannelID(Integer channelID) {
        this.channelID = channelID;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
