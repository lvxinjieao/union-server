package com.u8.server.web.pay.strategies;

import com.u8.server.data.UGame;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.utils.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * 支付回调协议中，新的签名计算规则
 *
 * 除了sign, signType之外的所有参数，按照字符串key升序排列， 然后按照key=value&key=value.....&appSecretKey生成待签名字符串
 *
 * 2019-03-15之后的版本，默认使用该方式
 */
public class NewNotifySignStrategy extends DefaultNotifySignStrategy {

    public NewNotifySignStrategy(String signType){
        super(signType);
    }

    @Override
    public String generateSign(UGame game, UOrder order) {

        Map<String,String> data = new HashMap<String,String>();
        data.put("channelID", order.getChannelID()+"");
        data.put("currency", order.getCurrency());
        data.put("extension", order.getExtension() == null ? "" : order.getExtension());
        data.put("gameID", order.getAppID()+"");
        data.put("money", order.getMoney()+"");
        data.put("orderID", order.getOrderID()+"");
        data.put("productID", order.getProductID() == null ? "" : order.getProductID());
        data.put("serverID", order.getServerID() == null ? "" : order.getServerID());
        data.put("userID", order.getUserID()+"");

        String signStr = StringUtils.generateUrlSortedParamString(data, "&", false);

        signStr = signStr + game.getAppSecret();

        String sign = sign(game, signStr, signType);

        Log.d("callback to game server sign str:%s", signStr);
        Log.d("callback to game server sign type:%s",signType);
        Log.d("callback to game server sign:%s", sign);

        return sign;
    }
}
