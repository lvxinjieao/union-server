package com.u8.server.web.admin;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UAdmin;
import com.u8.server.data.UAdminRole;
import com.u8.server.data.ULoginInfo;
import com.u8.server.data.USysMenu;
import com.u8.server.log.Log;
import com.u8.server.service.UAdminManager;
import com.u8.server.service.UAdminRoleManager;
import com.u8.server.service.USysMenuManager;
import com.u8.server.utils.CodeUtils;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.http.util.TextUtils;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.imageio.ImageIO;
import java.awt.image.RenderedImage;
import java.io.OutputStream;
import java.util.List;

/**
 * 后台管理主页
 * Created by ant on 2015/8/22.
 */
@Controller
@Namespace("/admin")
public class AdminIndexAction extends UActionSupport implements ModelDriven<UAdmin> {

    private UAdmin admin;

    @Autowired
    private UAdminManager adminManager;

    @Autowired
    private UAdminRoleManager adminRoleManager;

    @Autowired
    private USysMenuManager sysMenuManager;

    private Integer myRoleID;

    private Integer appID;

    private String vcode;
    private String newPwd;

    @Action(value = "login", results = {@Result(name = "success", location = "/WEB-INF/admin/loginNew2.jsp")})
    public String showLogin(){
        System.out.println("vcode:"+vcode);
        return "success";
    }

    @Action("getVCode")
    public void generateVCode(){
        try{

            CodeUtils.ImageCode code = CodeUtils.generateCodeAndPic();
            session.put("myVCode", code.getCode());

            response.setContentType("image/png");//必须设置响应内容类型为图片，否则前台不识别

            OutputStream os = response.getOutputStream(); //获取文件输出流

            ImageIO.write((RenderedImage)code.getImg(),"jpeg",os);//输出图片流

            os.flush();
            os.close();//关闭流

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Action("doLogin")
    public void login(){
        try{

            Log.d("The username is "+admin.getUsername()+";password:"+admin.getPassword());

            if(StringUtils.isEmpty(admin.getUsername())){
                renderState(false, "操作失败");
                return;
            }

            ULoginInfo loginInfo = (ULoginInfo) session.get("myLoginInfo"+admin.getUsername());
            if(loginInfo == null || System.currentTimeMillis() - loginInfo.getTime() >= 600000){
                loginInfo = new ULoginInfo(admin.getUsername(), 0);
                session.put("myLoginInfo"+admin.getUsername(), loginInfo);
            }

            int tryNum = loginInfo.addTryNum();

            if(tryNum > 10 ){   //10分钟内尝试10次错误验证码

                renderState(false, "重试次数过多，请10分钟后再试");
                return;
            }


            Object vcodeLocal = session.get("myVCode");
            if(StringUtils.isEmpty(vcode) || vcodeLocal == null || !vcodeLocal.toString().equalsIgnoreCase(vcode)){
                session.remove("myVCode");
                renderState(false, "验证码错误");
                return;
            }
            session.remove("myVCode");
            UAdmin exists = adminManager.getAdminByUsername(admin.getUsername());

            if(exists == null){
                renderState(false, "用户名错误");
                return;
            }

            if(!exists.getPassword().equals(admin.getPassword())){
                renderState(false, "用户密码错误");
                return;
            }
            this.session.remove("myLoginInfo"+admin.getUsername());
            this.session.put("adminName", exists.getUsername());
            this.session.put("admin", exists);
            renderState(true, "登录成功");

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Action("exit")
    public void exit(){
        this.session.remove("adminName");
        this.session.remove("admin");
        renderState(true, "exit success");
    }

    @Action("changePassword")
    public void changePassword(){

        if(StringUtils.isEmpty(this.admin.getPassword()) || StringUtils.isEmpty(newPwd)){
            renderState(false, "操作失败");
            return;
        }

        UAdmin admin =  (UAdmin)this.session.get("admin");

        if(!admin.getPassword().equals(this.admin.getPassword())){
            renderState(false, "密码错误");
            return;
        }

        admin.setPassword(newPwd);
        adminManager.saveAdmin(admin);

        renderState(true, "操作成功");

    }

    @Action(value = "index", results = {@Result(name = "success", location = "/WEB-INF/admin/index.jsp")})
    public String index(){


        return "success";
    }

    @Action(value = "adminRoleManage", results = {@Result(name = "success", location = "/WEB-INF/admin/admins.jsp")})
    public String showAdminRoleManage(){


        return "success";
    }


    @Action("getMyMenus")
    public void getMyMenus(){
        try{

            UAdmin admin = (UAdmin)session.get("admin");

            UAdminRole role = adminRoleManager.getAdminRoleByID(admin.getAdminRoleID());
            if(role == null){
                Log.e("role id is not exists:"+admin.getAdminRoleID());
                return;
            }

            List<USysMenu> menus = sysMenuManager.getMenusByPermission(role);

            JSONArray array = new JSONArray();
            for(USysMenu menu : menus){
                JSONObject json = menu.toJSON();
                if(menu.getChildren() != null && menu.getChildren().size() > 0){
                    JSONArray carray = new JSONArray();
                    for (USysMenu c : menu.getChildren()){
                        carray.add(c.toJSON());
                    }
                    json.put("childMenus", carray);
                }
                array.add(json);
            }

            Log.d(array.toString());

            renderJson(array.toString());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Action("getAllAdminList")
    public void getAllAdminList(){
        try{

            if(appID == null){
                appID = 0;
            }

            List<UAdmin> lst = adminManager.getAllAdmins();
            List<UAdmin> gameAdminLst = adminManager.getAllAdminsByGameID(appID);
            JSONArray masterArray = new JSONArray();
            for(UAdmin m : lst){
                int checked = 0;
                for(UAdmin mc : gameAdminLst){
                    if(mc.getId().equals(m.getId())){
                        checked = 1;
                    }
                }
                JSONObject json = new JSONObject();
                json.put("id", m.getId());
                json.put("username", m.getUsername());
                json.put("checkedState", checked);
                masterArray.add(json);
            }
            renderJson(masterArray.toString());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Action("getAllAdmins")
    public void getAllAdmins(){
        try{

            List<UAdminRole> roles = adminRoleManager.getAdminRoles();

            List<UAdmin> lst = adminManager.getAllAdmins();
            JSONObject json = new JSONObject();
            json.put("total", lst.size());
            JSONArray masterArray = new JSONArray();
            for(UAdmin m : lst){

                //如果是最高权限，返回界面展示的时候， 角色名称设置为【最高权限】
                for(UAdminRole r : roles){
                    if(r.getId().equals(m.getAdminRoleID())){
                        if(r.getTopRole() != null && r.getTopRole() == 1){
                            m.setAdminRoleName("【最高权限】"+m.getAdminRoleName());
                        }
                        break;
                    }
                }

                masterArray.add(m.toJSON());
            }
            json.put("rows", masterArray);


            renderJson(json.toString());

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //添加或者编辑
    @Action("saveAdmin")
    public void saveAdmin(){

        try{

            if(TextUtils.isEmpty(this.admin.getUsername()) || TextUtils.isEmpty(this.admin.getPassword())){
                renderState(false, "用户名和密码不能为空");
                return;
            }

            if(this.admin.getId() != null){
                UAdmin exist = adminManager.getAdmin(this.admin.getId());
                if(exist != null ){

                    if(!exist.getPassword().equals(this.admin.getPassword())){
                        //说明修改了密码，重新md5
                        exist.setPassword(EncryptUtils.md5(this.admin.getPassword()).toLowerCase());
                    }

                    this.admin = exist;
                }
            }else{
                this.admin.setPassword(EncryptUtils.md5(this.admin.getPassword()).toLowerCase());
            }

            UAdminRole role = adminRoleManager.getAdminRoleByID(this.myRoleID);
            if(role == null){
                renderState(false, "请指定一个权限角色");
                return;
            }

            this.admin.setAdminRoleID(role.getId());
            this.admin.setAdminRoleName(role.getRoleName());

            adminManager.saveAdmin(this.admin);
            renderState(true, "保存成功");

            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false, "保存失败");
    }

    @Action("removeAdmin")
    public void removeAdmin(){
        try{
            Log.d("Curr userID is " + this.admin.getId());

            UAdmin user = adminManager.getAdmin(this.admin.getId());

            if(user == null){
                renderState(false, "角色不存在");
                return;
            }

            adminManager.deleteAdmin(user);

            renderState(true, "删除成功");

            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false, "删除失败");
    }

    @Action(value = "adminPermissionManage", results = {@Result(name = "success", location = "/WEB-INF/admin/adminPermissions.jsp")})
    public String showPermissionManage(){

        return "success";
    }

    private void renderState(boolean suc, String msg){
        JSONObject json = new JSONObject();
        json.put("state", suc? 1 : 0);
        json.put("msg", msg);
        renderText(json.toString());
    }

    @Override
    public UAdmin getModel() {
        if(this.admin == null){
            this.admin = new UAdmin();
        }
        return this.admin;
    }

    public Integer getMyRoleID() {
        return myRoleID;
    }

    public void setMyRoleID(Integer myRoleID) {
        this.myRoleID = myRoleID;
    }

    public Integer getAppID() {
        return appID;
    }

    public void setAppID(Integer appID) {
        this.appID = appID;
    }

    public String getVcode() {
        return vcode;
    }

    public void setVcode(String vcode) {
        this.vcode = vcode;
    }

    public String getNewPwd() {
        return newPwd;
    }

    public void setNewPwd(String newPwd) {
        this.newPwd = newPwd;
    }
}
