package com.u8.server.web.pay.strategies;

import com.u8.server.data.UGame;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.RSAUtils;

/**
 * 默认的回调通知协议中， 签名计算排序算法规则【固定先后顺序】
 * roleID不参与签名计算
 * 2019-03-15之前的版本使用该算法
 */
public class DefaultNotifySignStrategy implements INotifySignStrategy {

    protected String signType;

    public DefaultNotifySignStrategy(){
        this.signType = "md5";
    }

    public DefaultNotifySignStrategy(String signType){
        this.signType = signType;
    }

    public String generateSign(UGame game, UOrder order){

        StringBuilder sb = new StringBuilder();
        sb.append("channelID=").append(order.getChannelID()).append("&")
                .append("currency=").append(order.getCurrency() == null ? "" : order.getCurrency()).append("&")
                .append("extension=").append(order.getExtension() == null ? "" : order.getExtension()).append("&")
                .append("gameID=").append(order.getAppID()).append("&")
                .append("money=").append(order.getMoney()).append("&")
                .append("orderID=").append(order.getOrderID()).append("&")
                .append("productID=").append(order.getProductID() == null ? "" : order.getProductID()).append("&")
                .append("serverID=").append(order.getServerID()).append("&")
                .append("userID=").append(order.getUserID())
                .append(game.getAppSecret());

        String sign = sign(game, sb.toString(), signType);

        Log.d("补单 callback to game server sign str:%s", sb.toString());
        Log.d("补单 callback to game server sign type:%s",signType);
        Log.d("补单 callback to game server sign:%s", sign);
        return sign;
    }

    @Override
    public String getSignType() {
        return this.signType;
    }

    protected String sign(UGame game, String signStr, String signType){
        String str ="";
        try {
            if("md5".equalsIgnoreCase(signType)){
                str = EncryptUtils.md5(signStr).toLowerCase();
            }else{
                str = RSAUtils.sign(signStr, game.getAppRSAPriKey());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

}
