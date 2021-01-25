package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.RSAUtils;
import com.u8.server.utils.StringUtils;
import com.u8.server.web.pay.SendAgent;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.Date;


/**
 * 三星新SDK支付回调
 */
@Controller
@Namespace("/pay/samsung")
public class SamsungPayCallbackAction extends UActionSupport {

    @Autowired
    private UOrderManager orderManager;

    @Autowired
    private UChannelManager channelManager;

    private String tranNo;//商户唯一订单号
    private String orderNo;//平台订单号
    private String amount;//实付金额（单位元）
    private String transState;//支付状态（(S:交易成功 F:交易失败 P:交易处理中)）
    private String transmit;//透传参数
    private String payment;//支付方式（0：电银微信 1：腾讯微信 2：支付宝）
    private String remarks;//渠道标记（固定值SAMSUNG）
    private String sign;


    @Action("payCallback")
    public void payCallback() {

        Log.d("samsung tranNo=" + tranNo);
        Log.d("samsung orderNo=" + orderNo);
        Log.d("samsung amount=" + amount);
        Log.d("samsung transState=" + transState);
        Log.d("samsung transmit=" + transmit);
        Log.d("samsung payment=" + payment);
        Log.d("samsung remarks=" + remarks);
        Log.d("samsung sign=" + sign);


        try {
            long orderID = Long.parseLong(tranNo);
            UOrder order = orderManager.getOrder(orderID);

            if (order == null || order.getChannel() == null) {
                Log.d("samsung new pay callback failed.The order is null or the channel is null.");
                this.renderState(false);
                return;
            }

            if (!"S".equalsIgnoreCase(transState)) {
                Log.d("samsung new pay callback failed.The order state is failed.state:%s; orderID:%s.", transState, orderID);
                this.renderState(false);
                return;
            }

            UChannel channel = channelManager.getChannel(order.getChannelID());
            if (channel == null) {
                Log.d("samsung new pay callback failed.channel id not exists:%s; orderID:%s.", order.getChannelID(), orderID);
                this.renderState(false);
                return;
            }

            if (!isSignOK(channel)) {
                Log.d("samsung pay callback failed.");
                Log.d("sign:%s;", sign);
                Log.d("publicKey:%s;", channel.getCpPayKey());//支付公钥
                this.renderState(false);
                return;
            }

            if (order.getState() > PayState.STATE_PAYING) {
                Log.d("samsung new pay callback failed.The state of the order is complete. The state is " + order.getState());
                this.renderState(true);
                return;
            }


            int moneyInt = (int) (Float.valueOf(amount) * 100);
            if (moneyInt < order.getMoney()) {
                Log.d("order:%s money not matched. local price:%s; remote price:%s", orderID, order.getMoney(), moneyInt);
                this.renderState(false);
                return;
            }

            order.setRealMoney(moneyInt);
            order.setSdkOrderTime("");
            order.setCompleteTime(new Date());
            order.setChannelOrderID(orderNo);
            order.setState(PayState.STATE_SUC);
            orderManager.saveOrder(order);
            SendAgent.sendCallbackToServer(this.orderManager, order);
        } catch (Exception e) {
            try {
                renderState(false);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            Log.e(e.getMessage(), e);
        }
    }

    private boolean isSignOK(UChannel channel) throws Exception {
        String data = String.format("tranNo=[%s]&orderNo=[%s]&amount=[%s]&transState=[%s]", tranNo, orderNo, amount, transState);
        Log.d("data:" + data);

        String publicKey = "";

        if (StringUtils.isEmpty(publicKey)) {
            publicKey = channel.getCpPayKey();//支付公钥
        }
        Log.d("publicKey:" + publicKey);

        return RSAUtils.verify(data, this.sign, publicKey);
    }


    private void renderState(boolean suc) throws IOException {
        JSONObject json = new JSONObject();
        json.put("reuslt", suc ? "SUCCESS" : "FAILED");
        renderJson(json.toString());
    }


    public String getTranNo() {
        return tranNo;
    }

    public void setTranNo(String tranNo) {
        this.tranNo = tranNo;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransState() {
        return transState;
    }

    public void setTransState(String transState) {
        this.transState = transState;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getTransmit() {
        return transmit;
    }

    public void setTransmit(String transmit) {
        this.transmit = transmit;
    }

    public String getPayment() {
        return payment;
    }

    public void setPayment(String payment) {
        this.payment = payment;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
