package com.u8.server.web.pay.strategies;

import com.u8.server.data.UGame;
import com.u8.server.data.UOrder;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 使用该类， 支付回调协议中， 用application/x-www-form-urlencoded格式通知回调游戏服务器
 */
public class OrderWWWResponseStrategy extends OrderResponseStrategy {

    public OrderWWWResponseStrategy(INotifySignStrategy strategy) {
        this.signStrategy = strategy;
    }

    @Override
    public HttpEntity generateResponseHttpEntity(UGame game, UOrder order) {
        String sign = signStrategy.generateSign(game, order);
        List<NameValuePair> pairs = new ArrayList<NameValuePair>();
        pairs.add(new BasicNameValuePair("channelID", order.getChannelID() + ""));
        pairs.add(new BasicNameValuePair("currency", order.getCurrency()));
        pairs.add(new BasicNameValuePair("extension", order.getExtension() == null ? "" : order.getExtension()));
        pairs.add(new BasicNameValuePair("gameID", order.getAppID() + ""));
        pairs.add(new BasicNameValuePair("money", order.getMoney() + ""));
        pairs.add(new BasicNameValuePair("orderID", order.getOrderID() + ""));
        pairs.add(new BasicNameValuePair("productID", order.getProductID() == null ? "" : order.getProductID()));
        pairs.add(new BasicNameValuePair("serverID", order.getServerID() == null ? "" : order.getServerID()));
        pairs.add(new BasicNameValuePair("userID", order.getUserID() + ""));
        pairs.add(new BasicNameValuePair("signType", signStrategy.getSignType()));
        pairs.add(new BasicNameValuePair("sign", sign));
        return new UrlEncodedFormEntity(pairs, Charset.forName("UTF-8"));
    }

    @Override
    public Map<String, String> generateReponseHeader() {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("Content-Type", "application/x-www-form-urlencoded");
        return headers;
    }
}
