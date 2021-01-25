package com.u8.server.filters;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.MethodFilterInterceptor;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UAdmin;
import com.u8.server.data.UAdminRole;
import com.u8.server.data.USysMenu;
import com.u8.server.log.Log;
import com.u8.server.service.UAdminRoleManager;
import com.u8.server.service.USysMenuManager;
import com.u8.server.utils.StringUtils;
import org.apache.struts2.ServletActionContext;
import org.springframework.beans.factory.annotation.Autowired;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * Created by ant on 2015/8/29.
 */
public class LoginInterceptor extends MethodFilterInterceptor {

    private List<USysMenu> menus;

    @Autowired
    private USysMenuManager menuManager;

    @Autowired
    private UAdminRoleManager adminRoleManager;


    private boolean isPathValid(Map session, String path){

        if(menus == null) {
            menus = menuManager.getAllMenus();
        }

        boolean isMenu = false;

        for(USysMenu menu : menus){
            if((!StringUtils.isEmpty(menu.getPath())) && path.contains(menu.getPath())){
                isMenu = true;
                break;
            }
        }

        if(!isMenu){

            return true;
        }

        UAdmin admin = (UAdmin)session.get("admin");
        Log.d("login interceptor: the path is "+path);
        UAdminRole role = adminRoleManager.getAdminRoleByID(admin.getAdminRoleID());
        if(role == null){
            Log.e("role id is not exists:"+admin.getAdminRoleID());
            return false;
        }

        if(role.getTopRole() == 1){
            return true;
        }

        List<USysMenu> permissionMenus = menuManager.getPlatMenusByPermission(role);

        if(permissionMenus != null){
            for(USysMenu m : permissionMenus){
                if((!StringUtils.isEmpty(m.getPath())) && path.contains(m.getPath())){
                    return true;
                }
            }
        }

        return false;

    }

    @Override
    protected String doIntercept(ActionInvocation actionInvocation) throws Exception {

        HttpServletRequest request= ServletActionContext.getRequest();
        String path=request.getRequestURI();

        Log.d("The path is" + path);


        if(path.contains("/admin") || path.contains("/analytics")){

            if(path.contains("/orders/queryOrders")){
                return actionInvocation.invoke();
            }

            UActionSupport action = (UActionSupport)actionInvocation.getAction();
            Map session = action.getSession();

            if(session.containsKey("adminName")){
                return actionInvocation.invoke();
            }else{
                return Action.LOGIN;
            }

        }else if(path.contains("/api")){
            return actionInvocation.invoke();
        }else{
            return Action.LOGIN;
        }



    }
}
