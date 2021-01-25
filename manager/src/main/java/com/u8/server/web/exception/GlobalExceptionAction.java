package com.u8.server.web.exception;

import com.u8.server.common.UActionSupport;
import com.u8.server.log.Log;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.stereotype.Controller;

/**
 * 全局异常处理
 * Created by ant on 2017/7/1.
 */
@Controller
@Namespace("/admin")
public class GlobalExceptionAction extends UActionSupport {

    @Action("exceptionHandle")
    public void exceptionHandle(){

        Log.d("request handle error.");
        renderState(false);
    }


    private void renderState(boolean suc){
        JSONObject json = new JSONObject();
        json.put("state", suc? 1 : 0);
        json.put("msg", suc? "操作成功" : "操作失败");
        renderText(json.toString());
    }

}
