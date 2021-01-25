package com.u8.server.filters;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.u8.server.log.Log;
import org.apache.struts2.ServletActionContext;

import javax.servlet.http.HttpServletRequest;

/**
 * Created by ant on 2015/8/29.
 */
public class LoginInterceptor extends MethodFilterInterceptor {


    @Override
    protected String doIntercept(ActionInvocation actionInvocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        String path = request.getRequestURI();
        Log.d("The path is" + path);
        return actionInvocation.invoke();

    }
}
