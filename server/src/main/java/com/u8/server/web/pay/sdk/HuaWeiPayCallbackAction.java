package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.constants.StateCode;
import com.u8.server.data.UChannel;
import com.u8.server.data.UGame;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UGameManager;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.RSAUtils;
import com.u8.server.utils.TimeUtils;
import com.u8.server.web.pay.SendAgent;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Date;

/**
 * 华为支付回调通知接口
 * Created by ant on 2020/9/22.
 */
@Controller
@Namespace("/pay/huawei")
public class HuaWeiPayCallbackAction extends UActionSupport {


   /* private String result;        //string(3)  支付结果：“0”：支付成功“1”：退款成功（暂未启用）
    private String userName;        //string  开发者社区用户名或联盟用户编号；终端sdk上报了联盟用户编号
    private String productName;        //string(100)  商品名称；对应APK填写的productName
    private String payType;        //int
    private String amount;        //string(10)  商品支付金额 (格式为：元.角分，最小精确到分，例如：20.01)
    private String currency;
    private String orderId;        //string(50)  华为订单号，为华为支付平台生成
    private String notifyTime;        //string(15)  通知时间，自1970年1月1日0时起的毫秒数
    private String requestId;        //string(30)  开发者支付请求ID
    private String bankId;        //string(50)  银行编码-支付通道信息，传递条件如下：只有在有具体取值时才传递
    private String orderTime;        //string  下单时间 yyyy-MM-dd hh:mm:ss；仅在sdk中指定了urlver为2 时有效。
    private String tradeTime;        //string  交易/退款时间 yyyy-MM-dd hh:mm:ss；仅在sdk中指定了urlver为2时有效。
    private String accessMode;        //string  接入方式：仅在sdk 中指定了urlver为2时有效。
    private String spending;        //string  渠道开销，保留两位小数，单位元。仅在sdk中指定了urlver为2时有效。
    private String extReserved;        //string  商户侧保留信息，原样返回商户调用支付sdk
    private String sysReserved;       //string 系统保留信息
    private String signType;
    private String sign;        //string  RSA签名。

    @Autowired
    private UOrderManager orderManager;

    @Autowired
    private UChannelManager channelManager;*/


    private String appID;
    private String purchaseData;
    private String purchaseSign;
    private String sign;

    public String getAppID() {
        return appID;
    }

    public void setAppID(String appID) {
        this.appID = appID;
    }

    public String getPurchaseData() {
        return purchaseData;
    }

    public void setPurchaseData(String purchaseData) {
        this.purchaseData = purchaseData;
    }

    public String getPurchaseSign() {
        return purchaseSign;
    }

    public void setPurchaseSign(String purchaseSign) {
        this.purchaseSign = purchaseSign;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    @Autowired
    private UOrderManager orderManager;

    @Autowired
    private UGameManager gameManager;


    @Action("checkPay")
    public void checkPay() {
        try {
            UGame game = gameManager.getGame(Integer.valueOf(appID));
            if (game == null) {
                renderState(StateCode.CODE_UNKNOWN_ERROR, "game not exists");
                return;
            }

            String signStr = "appID=" + appID + "&purchaseData=" + purchaseData + "&purchaseSign=" + purchaseSign + game.getAppkey();
            String signLocal = EncryptUtils.md5(signStr);

            if (!signLocal.equals(sign)) {
                Log.e("huawei checkPay sign not match. sign str:%s; sign local:%s; sign:%s", signStr, signLocal, sign);
                renderState(StateCode.CODE_UNKNOWN_ERROR, "sign not match");
                return;
            }

            JSONObject jsonData = JSONObject.fromObject(purchaseData);

            String huaweiOrderId = jsonData.getString("orderId");
            String payTime = jsonData.getString("purchaseTime");
            String purchaseToken = jsonData.getString("purchaseToken");
            String productID = jsonData.getString("productId");
            String orderID = jsonData.getString("developerPayload");
            String price = jsonData.getString("price");

            UOrder order = orderManager.getOrder(Long.parseLong(orderID));

            if (order == null) {
                Log.e("huawei checkPay order not exist. orderID:%s", orderID);
                renderState(StateCode.CODE_FAIL, "订单不存在", purchaseToken, orderID);
                return;
            }

            UChannel channel = order.getChannel();

            if (order.getState() > PayState.STATE_PAYING) {
                updateFailOrder(order, huaweiOrderId, price);
                Log.d("huawei checkPay state of the order is complete. orderID:%s; state:%s ", orderID, order.getState());
                this.renderState(StateCode.CODE_SUCCESS, "该订单已经被处理,或者CP订单号重复", purchaseToken, orderID);
                return;
            }

            Log.d("huawei checkPay. purchaseData:%s", purchaseData);
            Log.d("huawei checkPay. purchaseSign:%s", purchaseSign);
            Log.d("huawei checkPay. 应用内支付公钥:%s", channel.getCpPayKey());

            boolean huaweiSignOK = RSAUtils.verify(purchaseData, purchaseSign, channel.getCpPayKey());
            if (!huaweiSignOK) {
                updateFailOrder(order, huaweiOrderId, price);
                Log.e("huawei checkPay huawei pay sign check failed. orderID:%s,", orderID);
                Log.e("huawei checkPay huawei pay sign check failed. publicKey:%s", channel.getCpPayKey());
                this.renderState(StateCode.CODE_FAIL, "支付数据校验失败", purchaseToken, orderID);
                return;
            }

            int moneyInt = Integer.valueOf(price);
            if (order.getMoney() > moneyInt) {
                updateFailOrder(order, huaweiOrderId, price);
                Log.e("huawei checkPay money not match! local orderID:" + orderID + "; money returned:" + moneyInt + "; order money:" + order.getMoney());
                this.renderState(StateCode.CODE_FAIL, "金额不匹配", purchaseToken, orderID);
                return;
            }

            order.setChannelOrderID(huaweiOrderId);
            order.setRealMoney(moneyInt);
            order.setSdkOrderTime(TimeUtils.format_default(new Date(Long.valueOf(payTime))));
            order.setCompleteTime(new Date());
            order.setState(PayState.STATE_SUC);
            orderManager.saveOrder(order);
            SendAgent.sendCallbackToServer(this.orderManager, order);
            renderState(StateCode.CODE_SUCCESS, "success", purchaseToken, orderID);

        } catch (Exception e) {
            renderState(StateCode.CODE_UNKNOWN_ERROR, "checkPay with exception:" + e.getMessage());
            e.printStackTrace();
        }
    }

    private void updateFailOrder(UOrder order, String channelOrderId, String price) {
        order.setChannelOrderID(channelOrderId);
        order.setState(PayState.STATE_FAILED);
        order.setRealMoney(Integer.valueOf(price));
        orderManager.saveOrder(order);
    }

    private void renderState(int code, String msg) {
        renderState(code, msg, null, null);
    }

    private void renderState(int code, String msg, String purchaseToken, String orderID) {
        JSONObject json = new JSONObject();
        json.put("result", code);
        json.put("msg", msg);
        json.put("purchaseToken", purchaseToken);
        json.put("orderID", orderID);
        this.renderJson(json.toString());
    }

    /*@Action("payCallback")
    public void payCallback() {
        try {
            long localOrderID = Long.parseLong(requestId);

            UOrder order = orderManager.getOrder(localOrderID);

            if (order == null) {
                Log.d("The order is null %s.", localOrderID);
                this.renderState(3, "notifyId 错误");
                return;
            }

            if (order.getState() > PayState.STATE_PAYING) {
                Log.d("The state of the order is complete. The state is " + order.getState());
                this.renderState(0, "该订单已经被处理,或者CP订单号重复");
                return;
            }

            if (!"0".equals(result)) {
                Log.d("the order returned is failed. " + this.result);
                this.renderState(3, "支付平台返回的是失败订单");
                return;
            }

            UChannel channel = channelManager.getChannel(order.getChannelID());
            if (channel == null) {
                Log.e("the channel %s of order %s is not exists.", order.getChannelID(), localOrderID);
                this.renderState(3, "渠道不存在");
                return;
            }

            int moneyInt = (int) (Float.valueOf(amount) * 100);
            if (order.getMoney() > moneyInt) {
                Log.e("订单金额不一致! local orderID:" + localOrderID + "; money returned:" + moneyInt + "; order money:" + order.getMoney());
                this.renderState(3, "金额不匹配");
                return;
            }

            if (isValid(channel)) {
                order.setChannelOrderID(orderId);
                order.setRealMoney(moneyInt);
                order.setSdkOrderTime(orderTime);
                order.setCompleteTime(new Date());
                order.setState(PayState.STATE_SUC);
                orderManager.saveOrder(order);
                SendAgent.sendCallbackToServer(this.orderManager, order);

                this.renderState(0, "");
            } else {
                order.setChannelOrderID(orderId);
                order.setState(PayState.STATE_FAILED);
                orderManager.saveOrder(order);
                this.renderState(1, "sign 错误");
            }

        } catch (Exception e) {
            Log.e(e.getMessage(), e);
            try {
                this.renderState(94, "未知错误");
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
    }*/

    /*private boolean isValid(UChannel channel) throws UnsupportedEncodingException {

        Map<String, Object> params = new HashMap<String, Object>();

        if (!StringUtils.isEmpty(result)) {
            params.put("result", result);
        }

        if (!StringUtils.isEmpty(userName)) {
            params.put("userName", userName);
        }

        if (!StringUtils.isEmpty(productName)) {
            params.put("productName", productName);
        }

        if (!StringUtils.isEmpty(payType)) {
            params.put("payType", payType);
        }

        if (!StringUtils.isEmpty(amount)) {
            params.put("amount", amount);
        }

        if (!StringUtils.isEmpty(currency)) {
            params.put("currency", currency);
        }

        if (!StringUtils.isEmpty(orderId)) {
            params.put("orderId", orderId);
        }

        if (!StringUtils.isEmpty(notifyTime)) {
            params.put("notifyTime", notifyTime);
        }

        if (!StringUtils.isEmpty(requestId)) {
            params.put("requestId", requestId);
        }

        if (!StringUtils.isEmpty(bankId)) {
            params.put("bankId", bankId);
        }

        if (!StringUtils.isEmpty(orderTime)) {
            params.put("orderTime", orderTime);
        }

        if (!StringUtils.isEmpty(tradeTime)) {
            params.put("tradeTime", tradeTime);
        }


        if (!StringUtils.isEmpty(accessMode)) {
            params.put("accessMode", accessMode);
        }

        if (!StringUtils.isEmpty(spending)) {
            params.put("spending", spending);
        }

        if (!StringUtils.isEmpty(extReserved)) {
            params.put("extReserved", extReserved);
        }

        if (!StringUtils.isEmpty(sysReserved)) {
            params.put("sysReserved", sysReserved);
        }

        boolean suc = RSA.rsaDoCheck(params, sign, channel.getCpPayKey(), signType);
        Log.d("huawei pay callback sign check result:" + suc + " signType=" + signType + " for order:" + requestId + ";huawei orderID:" + orderId);
        return suc;
    }*/


    /*private void renderState(int code, String msg) throws IOException {
        JSONObject json = new JSONObject();
        json.put("result", code);
        this.renderJson(json.toString());
    }*/


}
