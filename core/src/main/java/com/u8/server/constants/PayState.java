package com.u8.server.constants;

/**
 * 支付状态
 */
public class PayState {
    public static final int STATE_PAYING = 1;           //等待支付状态。游戏服务器下单之后，订单状态就是支付中
    public static final int STATE_SUC = 2;              //支付成功状态。SDK支付完成通知到u8server，u8server处理完成就是这个状态
    public static final int STATE_COMPLETE = 3;         //订单完成状态。u8server通知游戏服务器，游戏服务器返回成功，订单切换为这个状态
    public static final int STATE_FAILED = 0;           //订单失败状态。

}
