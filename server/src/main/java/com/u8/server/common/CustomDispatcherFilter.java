package com.u8.server.common;

import com.u8.server.log.Log;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 自定义过滤器， 同时兼容servlet处理方式
 * Created by ant on 2017/12/24.
 */
public class CustomDispatcherFilter implements Filter{

    private List<String> includes = new ArrayList<String>();

    @Override
    public void init(FilterConfig config) throws ServletException {
        this.includes.addAll( Arrays.asList(config.getInitParameter("includeServlets").split(",")));
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain) throws IOException, ServletException {

        try{
            HttpServletRequest request = (HttpServletRequest)req;
            String target = request.getRequestURI();

            int t = target.lastIndexOf("?");
            if(t > 0){
                target = target.substring(0, t);
            }

            //Log.d("CustomDispatcherFilter try match:"+target);
            if(isMatch(target)){
                RequestDispatcher rdsp = request.getRequestDispatcher(target);
                Log.d("match servlet:"+target);
                rdsp.forward(req, resp);
            }else{
                chain.doFilter(req, resp);
            }

        }catch (Exception e){
            Log.e(e.getMessage(), e);
            chain.doFilter(req, resp);
        }
    }

    private boolean isMatch(String targetUrl){

        for(String path : includes){
            if(targetUrl.contains(path)){
                return true;
            }
        }

        return false;
    }

    @Override
    public void destroy() {

    }
}
