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
import com.u8.server.utils.StringUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Date;

/**
 * 单机游戏支付的时候， 不依赖登录。 对于没有登录的情况，也没有用户信息的情况， 客户端支付完成之后， 发送完成协议到u8server
 * u8server收到完成协议之后， 更新订单状态为完成状态
 * Created by ant on 2018/3/29.
 */
@Controller
@Namespace("/pay")
public class PayCompleteAction extends UActionSupport {

    private Integer appID;
    private Integer channelID;
    private String orderID;
    private String channelOrderID;
    private String t;
    private String sign;

    @Autowired
    private UChannelManager channelManager;

    @Autowired
    private UOrderManager orderManager;

    @Action("complete")
    public void payComplete() {
        try{

            UChannel channel = channelManager.getChannel(this.channelID);
            if(channel == null){
                Log.e("channel not exists. channelID:"+this.channelID);
                renderState(false);
                return;
            }

            UOrder order = orderManager.getOrder(Long.valueOf(orderID));

            if(order == null){
                Log.e("The order is null or the channel is null.orderID:%s", orderID);
                this.renderState(false);
                return;
            }

            UGame game = channel.getGame();

            StringBuilder sb = new StringBuilder();
            sb.append("appID=").append(game.getAppID())
                    .append("channelID=").append(channel.getChannelID())
                    .append("orderID=").append(order.getOrderID())
                    .append("channelOrderID=").append(StringUtils.isEmpty(channelOrderID) ? "" : channelOrderID)
                    .append("t=").append(t)
                    .append(game.getAppkey());

            String md5 = EncryptUtils.md5(sb.toString()).toLowerCase();

            Log.d("completePay: sign str:%s; sign local:%s; sign:%s", sb.toString(), md5, sign);

            if(!md5.equals(sign)){
                Log.e("completePay: sign not match.");
                this.renderState(false);
                return;
            }

            order.setState(PayState.STATE_COMPLETE);
            order.setCompleteTime(new Date());
            orderManager.saveOrder(order);
            this.renderState(true);

        }catch (Exception e){
            this.renderState(false);
            e.printStackTrace();
        }

    }

    private void renderState(boolean suc){
        super.renderText(suc ? "SUCCESS" : "FAIL");
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

    public String getChannelOrderID() {
        return channelOrderID;
    }

    public void setChannelOrderID(String channelOrderID) {
        this.channelOrderID = channelOrderID;
    }
}
