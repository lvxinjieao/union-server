package com.u8.server.web.pay.strategies;

import com.u8.server.data.UGame;
import com.u8.server.data.UOrder;

public interface INotifySignStrategy {

    String generateSign(UGame game, UOrder order);

    String getSignType();

}
