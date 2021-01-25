package com.u8.server.constants;

/**
 * 唯一状态码
 */
public class StateCode {
    public static final int CODE_UNKNOWN_ERROR = -1;        //非法错误，检查u8server部署是否正常
    public static final int CODE_FAIL = 0;
    public static final int CODE_SUCCESS = 1;
    public static final int CODE_GAME_NONE = 2;             //appID有误，请检查打包工具/games/games.xml中配置的appID和后台游戏管理中配置的是否一致
    public static final int CODE_CHANNEL_NONE = 3;          //渠道号有误，请检查打包工具/games/当前游戏/config.xml中该渠道配置中channel中的id是否和后台渠道管理中配置的一致
    public static final int CODE_MASTER_NONE = 4;           //渠道商不存在
    public static final int CODE_AUTH_FAILED = 5;           //登录认证失败， 去第三方平台登录认证失败
    public static final int CODE_USER_NONE = 6;             //用户不存在
    public static final int CODE_VERIFY_FAILED = 8;         //用户验证失败，脚本类是否存在，异常抛出等
    public static final int CODE_TOKEN_ERROR = 9;           //token验证失败，超时或者不匹配
    public static final int CODE_MONEY_ERROR = 10;          //金额必须大于0
    public static final int CODE_ORDER_ERROR = 11;          //下单错误， 脚本类找不到， 下单异常抛出等
    public static final int CODE_SIGN_ERROR = 12;           //签名验证失败
    public static final int CODE_PAY_CLOSED = 13;           //充值未开放
    public static final int CODE_CHANNEL_NOT_MATCH = 14;    //渠道和游戏没有对上
    public static final int CODE_ORDER_ID_FAILED = 15;      //生成订单号失败
    public static final int CODE_SUBCHANNEL_NONE = 16;      //子渠道不存在
    public static final int CODE_UNICHANNEL_ERROR = 17;     //互通号错误

}
