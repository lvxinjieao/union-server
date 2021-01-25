package com.u8.server.web.pay.strategies;

import com.u8.server.data.UGame;
import com.u8.server.data.UOrder;
import org.apache.http.HttpEntity;

import java.util.Map;

public abstract class OrderResponseStrategy {

    protected INotifySignStrategy signStrategy;

    public abstract HttpEntity generateResponseHttpEntity(UGame game, UOrder order);

    public abstract Map<String, String> generateReponseHeader();
}
