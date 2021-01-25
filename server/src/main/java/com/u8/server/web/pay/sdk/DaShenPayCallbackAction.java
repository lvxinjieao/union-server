package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.web.pay.SendAgent;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;

@Controller
@Namespace("/pay/dashen")
public class DaShenPayCallbackAction extends UActionSupport {

    private static final long serialVersionUID = 1L;


    private String appId;       //CP游戏ID
    private String orderId;     //订单号
    private String uid;         //我方用户平台ID
    private String amount;      //订单金额(单位：元)
    private String serverId;    //游戏服务器编号
    private String extraInfo;   //cp调用SDK是传入信息原样返回
    private String test;        //是否是测试单 1为测试 其他为正常单(我方标识 CP方不作处理)
    private String orderTime;   //下单时间 (时间戳)
    private String billno;      //cp订单号
    private String sign;        //签名

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getExtraInfo() {
        return extraInfo;
    }

    public void setExtraInfo(String extraInfo) {
        this.extraInfo = extraInfo;
    }

    public String getTest() {
        return test;
    }

    public void setTest(String test) {
        this.test = test;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getBillno() {
        return billno;
    }

    public void setBillno(String billno) {
        this.billno = billno;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }


    @Autowired
    private UOrderManager orderManager;

    @Action("payCallback")
    public void payCallback() {

        Log.i("【########## appId : " + appId);
        Log.i("【########## orderId : " + orderId);
        Log.i("【########## uid : " + uid);
        Log.i("【########## amount : " + amount);
        Log.i("【########## serverId : " + serverId);
        Log.i("【########## extraInfo : " + extraInfo);
        Log.i("【########## test : " + test);
        Log.i("【########## orderTime : " + orderTime);
        Log.i("【########## billno : " + billno);
        Log.i("【########## sign : " + sign);

        Long orderID = Long.parseLong(billno);
        UOrder order = orderManager.getOrder(orderID);

        if (order == null || order.getChannel() == null) {
            Log.d("【The order is null or the channel is null】");
            this.renderState(false);
            return;
        }

        if (order.getState() > 1) {
            Log.d("The state of the order is complete. The state is " + order.getState());
            this.renderState(false);
            return;
        }

        UChannel channel = order.getChannel();
        if (channel == null) {
            this.renderState(false);
            return;
        }

        if (verifyPay(channel, amount, appId, billno, extraInfo, orderId, orderTime, serverId, test, uid, sign)) {
            order.setRealMoney((int) priceToFloat(amount) * 100);
            order.setCompleteTime(new Date());
            order.setChannelOrderID(orderId);
            order.setState(PayState.STATE_SUC);
            orderManager.saveOrder(order);
            SendAgent.sendCallbackToServer(this.orderManager, order);
            this.renderState(true);
        } else {
            order.setChannelOrderID(orderId);
            order.setState(PayState.STATE_FAILED);
            orderManager.saveOrder(order);
        }

    }

    public float priceToFloat(String price) {
        float p = 0;
        try {
            p = Float.parseFloat(price);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return p;
    }

    public boolean verifyPay(UChannel channel, String amount, String appId, String billno, String extraInfo, String orderId, String orderTime, String serverId, String test, String uid, String sign) {
        StringBuilder builder = new StringBuilder();
        builder.append("amount").append(amount);
        builder.append("appId").append(channel.getCpAppID());
        builder.append("billno").append(billno);
        builder.append("extraInfo").append(extraInfo);
        builder.append("orderId").append(orderId);
        builder.append("orderTime").append(orderTime);
        builder.append("serverId").append(serverId);
        builder.append("test").append(test);
        builder.append("uid").append(uid);
        builder.append(channel.getCpPayKey());// 游戏key

        String trim = builder.toString().trim();
        Log.i("【########## md5  前 : " + trim);
        String md5 = EncryptUtils.md5(trim);
        Log.i("【########## md5  后 : " + md5);
        Log.i("【########## sign 值 : " + sign);
        return sign.equals(md5);
    }

    private void renderState(boolean suc) {

        JSONObject json = new JSONObject();
        json.put("status", "success");
        json.put("msg", "");

        PrintWriter out = null;
        if (suc) {
            try {
                out = this.response.getWriter();
                out.write(json.toString());
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
