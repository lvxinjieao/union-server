package com.u8.server.web.pay.strategies;

import com.u8.server.constants.StateCode;
import com.u8.server.data.UGame;
import com.u8.server.data.UOrder;
import net.sf.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.entity.ByteArrayEntity;

import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

/**
 * 使用该类， 支付回调协议中， 用application/json格式通知回调游戏服务器
 */
public class OrderJsonResponseStrategy extends OrderResponseStrategy {

    public OrderJsonResponseStrategy(INotifySignStrategy strategy){
        this.signStrategy = strategy;
    }

    @Override
    public HttpEntity generateResponseHttpEntity(UGame game, UOrder order) {
        JSONObject data = new JSONObject();
        data.put("productID", order.getProductID() == null ? "" : order.getProductID());
        data.put("orderID", order.getOrderID());
        data.put("userID", order.getUserID());
        data.put("channelID", order.getChannelID());
        data.put("gameID", order.getAppID());
        data.put("serverID", order.getServerID() == null ? "" : order.getServerID());
        data.put("money", order.getMoney());
        data.put("currency", order.getCurrency());
        data.put("extension", order.getExtension() == null ? "" : order.getExtension());
        data.put("signType", signStrategy.getSignType());
        String sign = signStrategy.generateSign(game, order);
        data.put("sign", sign);


        JSONObject response = new JSONObject();
        response.put("state", StateCode.CODE_SUCCESS);
        response.put("data", data);
        return new ByteArrayEntity(response.toString().getBytes(Charset.forName("UTF-8")));
    }

    @Override
    public Map<String, String> generateReponseHeader() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/json");
        return headers;
    }
}
