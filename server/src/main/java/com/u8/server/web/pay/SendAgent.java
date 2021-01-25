package com.u8.server.web.pay;

import com.u8.server.cache.UApplicationContext;
import com.u8.server.config.GlobalConfig;
import com.u8.server.constants.PayState;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.sdk.UHttpAgent;
import com.u8.server.service.UOrderManager;
import com.u8.server.service.UUserManager;
import com.u8.server.task.OrderTaskManager;
import com.u8.server.utils.StringUtils;
import com.u8.server.web.pay.strategies.*;
import org.apache.http.HttpEntity;
import org.apache.http.util.EntityUtils;

import java.nio.charset.Charset;
import java.util.Date;
import java.util.Map;

/**
 * U8Server向游戏服发送回调通知
 */
public class SendAgent {

    public static final String SIGN_MD5 = "md5";
    public static final String CONTENT_TYPE_JSON = "application/json";

//    public static final String SIGN_RSA = "rsa";
//    public static final String CONTENT_TYPE_WWW = "application/x-www-form-urlencoded";

    private static String contentType = CONTENT_TYPE_JSON;
    private static String signType = SIGN_MD5;

    private static OrderResponseStrategy responseStrategy;
    private static INotifySignStrategy signStrategy;

    private static OrderResponseStrategy getCurrResponseStrategy() {

        if (responseStrategy != null) {
            return responseStrategy;
        }

        GlobalConfig config = (GlobalConfig) UApplicationContext.getBean("globalConfig");
        contentType = config.getPayContentType();
        signType = config.getPaySignMethod();

//      signStrategy = new DefaultNotifySignStrategy(signType);  //2019-03-15之前的版本，使用默认签名计算规则
        signStrategy = new NewNotifySignStrategy(signType);      //2019-03-15之后的版本，使用新的签名计算规则
        if (CONTENT_TYPE_JSON.equals(contentType)) {
            responseStrategy = new OrderJsonResponseStrategy(signStrategy);
        } else {
            responseStrategy = new OrderWWWResponseStrategy(signStrategy);
        }
        return responseStrategy;
    }

    /**
     * U8Server支付成功，通知游戏服务器，该接口在收到渠道SDK的支付回调时，进行调用
     * 协议格式，通过上面contentType指定；签名方式通过signType指定。
     *
     * @param orderManager
     * @param order
     * @return
     */
    public static boolean sendCallbackToServer(UOrderManager orderManager, UOrder order) {

        //记录当天的累充数额：
        orderManager.addMoneySum(order);
        orderManager.addPayedUser(order);

        UUserManager userManager = (UUserManager) UApplicationContext.getBean("userManager");
        UUser user = userManager.getUser(order.getUserID());
        if (user != null && (user.getFirstCharge() == null || user.getFirstCharge() == 0)) {
            //记录玩家首冲信息
            user.setFirstCharge(1);
            user.setFirstChargeTime(new Date());
            userManager.saveUser(user);
        }
        return sendToGameServer(orderManager, order, true);
    }

    /**
     * U8Server订单补发接口
     * 协议格式，通过上面contentType指定；签名方式通过signType指定。
     *
     * @param orderManager
     * @param order
     * @return
     */
    public static boolean resendCallbackToServer(UOrderManager orderManager, UOrder order) {
        return sendToGameServer(orderManager, order, false);
    }

    private static boolean sendToGameServer(UOrderManager orderManager, UOrder order, boolean addToTask) {

        try {
            if (order.getGame().isSingleGame()) { //纯单机， 就不用发送了
                return false;
            }

            String callbackUrl = order.getNotifyUrl();
            if (StringUtils.isEmpty(callbackUrl) || "null".equalsIgnoreCase(callbackUrl)) {
                callbackUrl = order.getGame().getPayCallback();
            }

            if (StringUtils.isEmpty(callbackUrl)) {
                Log.d("游戏没有配置回调地址");
                return false;
            }

            OrderResponseStrategy strategy = getCurrResponseStrategy();
            HttpEntity requestData = strategy.generateResponseHttpEntity(order.getGame(), order);
            Map<String, String> headers = strategy.generateReponseHeader();

            Log.d("聚合服务器 appId :%s", order.getAppID());
            Log.d("聚合服务器 url :%s", callbackUrl);
            Log.d("聚合服务器========>>>>>>>>>>>>游戏服务器 :%s", EntityUtils.toString(requestData, Charset.forName("UTF-8")));
            String serverRes = UHttpAgent.getInstance().post(callbackUrl, headers, requestData);
            Log.d("游戏服务器========>>>>>>>>>>>> 聚合服务器 :%s", serverRes);

            if ("SUCCESS".equals(serverRes)) {
                order.setState(PayState.STATE_COMPLETE);
                order.setCompleteTime(new Date());
                orderManager.saveOrder(order);
                return true;
            }
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
        }

        if (addToTask) { //失败了，加入重发队列，尝试6次
            OrderTaskManager.getInstance().addOrder(order);
        }
        return false;
    }


}
