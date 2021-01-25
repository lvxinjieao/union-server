package com.u8.server.web.test;

import com.u8.server.common.UActionSupport;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.stereotype.Controller;

/**
 * 测试游戏接收U8Server支付通知回调
 * Created by ant on 2017/7/8.
 */
@Controller
@Namespace("/user/test")
public class GamePayCallbackAction extends UActionSupport {


    @Action("payCallback")
    public void payCallback(){

        //TODO

        renderText("SUCCESS");

    }

}
