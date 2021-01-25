package com.u8.server.web.pay.sdk;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UChannel;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.XmlUtils;
import com.u8.server.sdk.quick.QuickSDKUtil;
import com.u8.server.web.pay.SendAgent;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.*;

/**
 * QuickSDK支付回调类
 * Created by kid on 18/4/3.
 */
@Controller
@Namespace("/pay/quick")
public class QuickPayCallbackAction extends UActionSupport {

    private String nt_data;         //通知数据解码后为xml格式 ,具体见2.1.1
    private String sign;            //签名
    private String md5Sign;         //计算方法为MD5(nt_data+sign+md5key),三个值直接拼接即可

    private Integer u8ChannelID;

    @Autowired
    private UOrderManager orderManager;

    @Autowired
    private UChannelManager channelManager;

    @Action("payCallback")
    public void payCallback() {
        try {

            Log.d("QuickPayCallbackAction sign start.u8ChannelID:" + u8ChannelID);
            UChannel channel = channelManager.getChannel(u8ChannelID);
            if (channel == null) {
                Log.d("The channel is not exists of channelID:" + u8ChannelID);
                this.renderState(false, null);
                return;
            }


            Log.d("nt_data=" + nt_data);
            Log.d("sign=" + sign);

            String decode_sign = QuickSDKUtil.decode(sign, channel.getCpAppSecret());//AppSecret -- callbackkey
            Log.d("decode_sign is " + decode_sign);

            String decode_nt_data = QuickSDKUtil.decode(nt_data, channel.getCpAppSecret());
            Log.d("decode_nt_data is " + decode_nt_data);

            Map<String, String> map = readStringXmlOut(decode_nt_data);
            if (map == null) {
                Log.d("parse xml error!");
                this.renderState(false, null);
                return;
            }

            String order_no = map.get("order_no");
            String game_order = map.get("game_order");
            Log.d("order_no=" + order_no + "; game_order=" + game_order);

            long orderID = Long.parseLong(game_order);

            UOrder order = orderManager.getOrder(orderID);

            if (order == null) {
                Log.d("The order is null or the channel is null.orderID:%s", orderID);
                this.renderState(false, null);
                return;
            }
            Log.d("orderID:%s", orderID);

            if (order.getState() > PayState.STATE_PAYING) {
                Log.d("The state of the order is complete. orderID:%s;state:%s", orderID, order.getState());
                this.renderState(true, channel);
                return;
            }
            Log.d("state:%s", order.getState());

            if (!isSignOK(decode_nt_data, decode_sign, channel)) {
                Log.d("the sign is not valid. sign:%s;orderID:%s", sign, order_no);
                this.renderState(false, channel);
                return;
            }


            int money = (int) (100 * Float.valueOf(map.get("amount")));
            String pay_time = map.get("pay_time");
            Log.d("QuickPayCallbackAction. money=" + money + "; pay_time=" + pay_time);

            order.setRealMoney(money);
            order.setSdkOrderTime(pay_time);
            order.setCompleteTime(new Date());
            order.setChannelOrderID(order_no);
            order.setState(PayState.STATE_SUC);

            orderManager.saveOrder(order);

            this.renderState(true, channel);

            SendAgent.sendCallbackToServer(this.orderManager, order);


        } catch (Exception e) {
            try {
                renderState(false, null);
            } catch (IOException e1) {
                e1.printStackTrace();
            }
            e.printStackTrace();
            Log.e("pay back error :%s", e.getMessage());
        }
    }

    /**
     * · 回调接口会接受到nt_data、sign、md5Sign 这3项参数
     * · 游戏采用md5(nt_data+sign+md5key)计算得到一个当前的md5SignLocal
     * · 游戏对比本地计算的md5SignLocal是否与接收到的md5Sign一致.一致则签名通过,不一致则签名验证失败.
     * · md5Key由QuickSDK分配,可从QuickSDK后台获取.
     */
    private boolean isSignOK(String decode_nt_data, String decode_sign, UChannel channel) {
        String data = this.nt_data + this.sign + channel.getCpPayKey();//md5key -- 支付公钥
        Log.d("sign data : " + data);
        String md5Local = EncryptUtils.md5(data);//生成sign
        Log.d("md5Local : " + md5Local);
        Log.d("md5Sign : " + this.md5Sign);
        //验证sign
        return md5Local.equals(this.md5Sign);

    }

    private void renderState(boolean suc, UChannel channel) throws IOException {
        try {
            String msg = suc ? "SUCCESS" : "FAILED";
            renderJson(msg);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getNt_data() {
        return nt_data;
    }

    public void setNt_data(String nt_data) {
        this.nt_data = nt_data;
    }

    public String getMd5Sign() {
        return md5Sign;
    }

    public void setMd5Sign(String md5Sign) {
        this.md5Sign = md5Sign;
    }

    public Integer getU8ChannelID() {
        return u8ChannelID;
    }

    public void setU8ChannelID(Integer u8ChannelID) {
        this.u8ChannelID = u8ChannelID;
    }


    /**
     * @description 将xml字符串转换成map
     *
     * <quicksdk_message>
     * <message>
     * <is_test>1</is_test>
     * <channel>0</channel>
     * <channel_name></channel_name>
     * <channel_uid>701e82380aef3b6f7a8f2684fd94dbb6@_()</channel_uid>
     * <game_order>1524666884174118929</game_order>
     * <order_no>00020180420111752206985243</order_no>
     * <pay_time>2018-04-20 11:17:52</pay_time>
     * <amount>1.00</amount>
     * <status>0</status>
     * <extras_params>http://localhost/webserver/pay/quick/payCallback/1601</extras_params>
     * </message>
     * </quicksdk_message>
     */
    public static Map readStringXmlOut(String xml) {
        Map map = new HashMap();
        Document doc = null;
        try {
            // 将字符串转为XML
            doc = DocumentHelper.parseText(xml);
            // 获取根节点
            Element rootElt = doc.getRootElement();
            // 拿到根节点的名称
            Log.d("root：" + rootElt.getName());

            // 获取根节点下的子节点head
            Iterator iter = rootElt.elementIterator("message");
            // 遍历head节点
            while (iter.hasNext()) {
                Element itemEle = (Element) iter.next();
                // 拿到head下的子节点script下的字节点username的值
                String is_test = itemEle.elementTextTrim("is_test");
                String channel = itemEle.elementTextTrim("channel");
                String channel_name = itemEle.elementTextTrim("channel_name");
                String channel_uid = itemEle.elementTextTrim("channel_uid");
                String game_order = itemEle.elementTextTrim("game_order");
                String order_no = itemEle.elementTextTrim("order_no");
                String pay_time = itemEle.elementTextTrim("pay_time");
                String amount = itemEle.elementTextTrim("amount");
                String status = itemEle.elementTextTrim("status");
                String extras_params = itemEle.elementTextTrim("extras_params");
                map.put("is_test", is_test);
                map.put("channel", channel);
                map.put("channel_name", channel_name);
                map.put("channel_uid", channel_uid);
                map.put("game_order", game_order);
                map.put("order_no", order_no);
                map.put("pay_time", pay_time);
                map.put("channel", channel);
                map.put("amount", amount);
                map.put("status", status);
                map.put("extras_params", extras_params);
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }
}
