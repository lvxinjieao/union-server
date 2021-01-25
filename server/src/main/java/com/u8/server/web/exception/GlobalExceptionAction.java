package com.u8.server.web.exception;

import com.u8.server.common.UActionSupport;
import com.u8.server.constants.StateCode;
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
@Namespace("/user")
public class GlobalExceptionAction extends UActionSupport {

    @Action("exceptionHandle")
    public void exceptionHandle() {
        Log.d("request handle error.");
        renderState(StateCode.CODE_UNKNOWN_ERROR, null);
    }


    private void renderState(int state, JSONObject data) {
        try {
            JSONObject json = new JSONObject();
            json.put("state", state);
            json.put("data", data);
            super.renderJson(json.toString());
        } catch (Exception e) {
            Log.e(e.getMessage(), e);
            Log.e(e.getMessage());
        }
    }

}
