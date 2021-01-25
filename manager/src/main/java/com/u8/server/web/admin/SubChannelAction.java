package com.u8.server.web.admin;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.common.Page;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UAdmin;
import com.u8.server.data.UChannel;
import com.u8.server.data.USubChannel;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import com.u8.server.service.UAdminManager;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.USubChannelManager;
import com.u8.server.service.UUserManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 子渠道
 * Created by ant on 2017/2/8.
 */
@Controller
@Namespace("/admin/subChannels")
public class SubChannelAction extends UActionSupport implements ModelDriven<USubChannel> {

    private int page;           //当前请求的页码
    private int rows;           //当前每页显示的行数

    private USubChannel channel;

    @Autowired
    private UAdminManager adminManager;

    @Autowired
    private USubChannelManager subChannelManager;

    @Autowired
    private UChannelManager channelManager;

    @Autowired
    private UUserManager userManager;


    @Action(value = "subChannelManage", results = {@Result(name = "success", location = "/WEB-INF/admin/subChannels.jsp")})
    public String subChannelManage(){

        return "success";
    }



    @Action("getAllSubChannels")
    public void getAllSubChannels(){
        try{

            UAdmin admin = (UAdmin)session.get("admin");
            Object[] ids = adminManager.getPermissonedGameIDs(admin);

            Page<USubChannel> currPage = subChannelManager.queryPage(page, rows, channel.getChannelID(), channel.getAppID(), channel.getSubChannelID(), ids);
            JSONObject json = new JSONObject();

            json.put("total", currPage == null ? 0 : currPage.getTotalCount());
            JSONArray users = new JSONArray();
            if(currPage != null){
                for(USubChannel m : currPage.getResultList()){
                    users.add(m.toJSON());
                }
            }

            json.put("rows", users);

            renderJson(json.toString());


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    //添加或者编辑
    @Action("saveSubChannel")
    public void saveSubChannel(){

        try{

            Log.d("add.subChannel.info." + this.channel.toJSON().toString());

            if(channel.getId() == null || channel.getId() <= 0){
                //新增的时候判断
                if(subChannelManager.isSubChannelValid(this.channel.getChannelID(), this.channel.getSubChannelID())){
                    renderState(false, "相同的子渠道已经存在");
                    return;
                }
            }

            UChannel parent = channelManager.getChannel(this.channel.getChannelID());
            if(parent == null){
                renderState(false, "父渠道不存在");
                return;
            }

            this.channel.setMasterID(parent.getMasterID());

            if(this.channel.getOpenPayFlag() == null){
                this.channel.setOpenPayFlag(0);
            }

            subChannelManager.saveSubChannel(this.channel);
            renderState(true, "操作成功");

            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false, "操作失败");
    }


    @Action("removeChannel")
    public void removeChannel(){
        try{

            USubChannel subChannel = this.subChannelManager.getSubChannel(this.channel.getChannelID(), this.channel.getSubChannelID());
            if(subChannel == null){
                renderState(false, "子渠道不存在");
                return;
            }

            List<UUser> lst = this.userManager.getUsersBySubChannel(subChannel.getChannelID(), subChannel.getSubChannelID());
            if(lst.size() > 0){
                renderState(false, "请先删除该子渠道下面的所有用户数据");
                return;
            }

            this.subChannelManager.deleteSubChannel(subChannel);

            renderState(true, "操作成功");
            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false, "操作失败");
    }

    private void renderState(boolean suc, String msg){
        JSONObject json = new JSONObject();
        json.put("state", suc? 1 : 0);
        json.put("msg", msg);
        renderText(json.toString());
    }



    @Override
    public USubChannel getModel() {
        if(this.channel == null){
            this.channel = new USubChannel();
        }

        return this.channel;
    }

    public USubChannel getChannel() {
        return this.channel;
    }

    public void setChannel(USubChannel channel) {
        this.channel = channel;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }
}
