package com.u8.server.web.admin;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.common.Page;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.*;
import com.u8.server.log.Log;
import com.u8.server.service.*;
import com.u8.server.utils.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.ArrayList;
import java.util.List;

/**
 * 渠道管理
 * Created by ant on 2015/8/22.
 */
@Controller
@Namespace("/admin/channels")
public class ChannelAction extends UActionSupport implements ModelDriven<UChannel> {

    private int page;           //当前请求的页码
    private int rows;           //当前每页显示的行数

    private UChannel channel;

    private int currAppID;
    private int currChannelID;
    private String searchMaserName;
    private String searchGameName;

    private String currChannelParams;

    @Autowired
    private UChannelManager channelManager;

    @Autowired
    private UUserManager userManager;

    @Autowired
    private UChannelMasterManager channelMasterManager;

    @Autowired
    private UAdminManager adminManager;

    @Autowired
    private UGameManager gameManager;



    @Action(value = "channelManage", results = {@Result(name = "success", location = "/WEB-INF/admin/channels.jsp")})
    public String channelManage(){

        return "success";
    }

    @Action("getAllChannels")
    public void getAllChannels(){
        try{


            List<Integer> masterIDs = null;
            if(!StringUtils.isEmpty(searchMaserName)){
                masterIDs = new ArrayList<Integer>();
                List<UChannelMaster> masters = channelMasterManager.getMastersByName(searchMaserName);
                if(masters != null){
                    for(UChannelMaster m : masters){
                        masterIDs.add(m.getMasterID());
                    }
                }
                if(masterIDs.size() == 0){
                    masterIDs.add(0);
                }
            }

            List<Object> matchedIds = null;

            UAdmin admin = (UAdmin)session.get("admin");
            Object[] ids = adminManager.getPermissonedGameIDs(admin);


            if(!StringUtils.isEmpty(searchGameName)){
                matchedIds = new ArrayList<Object>();
                List<UGame> games = gameManager.getGameByName(searchGameName);
                if(games != null && games.size() > 0){
                    for(UGame g : games){
                        for(Object gid : ids){
                            if(gid.equals(g.getAppID())){
                                matchedIds.add(gid);
                                break;
                            }
                        }
                    }
                }
            }

            Page<UChannel> currPage = channelManager.queryPage(page, rows, channel.getChannelID(), 0, masterIDs == null ? null : masterIDs.toArray(), matchedIds == null ? ids : matchedIds.toArray());
            JSONObject json = new JSONObject();

            json.put("total", currPage == null ? 0 : currPage.getTotalCount());
            JSONArray users = new JSONArray();
            if(currPage != null){
                for(UChannel m : currPage.getResultList()){
                    users.add(m.toJSON());
                }
            }

            json.put("rows", users);

            renderJson(json.toString());


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Action("getAllChannelsSimple")
    public void getAllChannelsSimple(){
        try{
            List<UChannel> channels = this.channelManager.getAllChannelsByAppID(this.currAppID);
            List<UChannelMaster> msters = this.channelMasterManager.queryAll();
            JSONArray array = new JSONArray();
            for(UChannel c : channels){
                JSONObject item = new JSONObject();
                item.put("channelID", c.getChannelID());

                String name = "null";
                for(UChannelMaster m : msters){
                    if(m.getMasterID() == c.getMasterID()){
                        name = m.getMasterName();
                        break;
                    }
                }

                item.put("showName", name+"("+c.getChannelID()+")");
//                item.put("showName", c.getChannelID()+"("+c.getMaster().getMasterName()+")");
                array.add(item);
            }

            renderJson(array.toString());

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Action("recommendChannelID")
    public void recommendChannelID(){
        try{

            int channelID = channelManager.getValidChannelID();

            JSONObject json = new JSONObject();
            json.put("state", 1);
            json.put("data", channelID);
            renderJson(json.toString());

        }catch (Exception e){
            renderState(false);
            e.printStackTrace();
        }
    }

    //添加或者编辑
    @Action("addChannel")
    public void addChannel(){

        try{
            Log.d("add.channel.info." + this.channel.toJSON().toString());

            UChannel exists = channelManager.getChannel(this.channel.getChannelID());
            if(exists != null){
                renderState(false, "操作失败,当前渠道号已经存在");
                return;
            }

            if(this.channel.getOpenPayFlag() == null){
                this.channel.setOpenPayFlag(0);
            }

            if(this.channel.getUniChannel() == null){
                this.channel.setUniChannel(0);
            }

            channelManager.saveChannel(this.channel);
            renderState(true);

            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false);
    }

    //添加或者编辑
    @Action("saveChannel")
    public void saveChannel(){

        try{
            Log.d("save.channel.info." + this.channel.toJSON().toString());

            if(this.channel.getOpenPayFlag() == null){
                this.channel.setOpenPayFlag(0);
            }

            if(this.channel.getUniChannel() == null){
                this.channel.setUniChannel(0);
            }

            if(this.channel.getId() == null || this.channel.getId() == 0){
                channelManager.saveChannel(this.channel);
                renderState(true);

                return;
            }else{
                UChannel exists = channelManager.getChannelByID(this.channel.getId());
                if(exists == null){
                    renderState(false);
                    return;
                }

                if(exists.getChannelID() != this.channel.getChannelID()){
                    channelManager.deleteChannelFromCache(exists);
                }
                exists.copy(this.channel);
                channelManager.saveChannel(exists);
                renderState(true);
            }


            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false);
    }

    @Action("removeChannel")
    public void removeChannel(){
        try{

            Log.d("Curr channelID is "+this.currChannelID);
            UChannel c = this.channelManager.getChannel(this.currChannelID);
            if(c == null){
                renderState(false);
                return;
            }

            List<UUser> lst = this.userManager.getUsersByChannel(this.currChannelID);
            if(lst.size() > 0){
                renderState(false, "请先删除该渠道下面的所有用户数据");
                return;
            }

            this.channelManager.deleteChannel(c);

            renderState(true);
            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false);
    }

    @Action("getChannelParams")
    public void getChannelParams(){

        try{
            Log.d("Curr channelID is "+this.currChannelID);
            UChannel c = this.channelManager.getChannel(this.currChannelID);
            if(c == null){
                renderState(false);
                return;
            }

            List<UChannelParamMeta> metas = channelManager.getParamMetasByChannel(c);

            JSONObject json = new JSONObject();

            json.put("state", "1");
            JSONArray metaArray = new JSONArray();

            if(metas != null){
                for(UChannelParamMeta m : metas){
                    metaArray.add(m.toJSON());
                }
            }


            json.put("data", metaArray);

            renderJson(json.toString());


        }catch (Exception e){
            renderState(false);
            e.printStackTrace();
        }

    }

    @Action("saveChannelParams")
    public void saveChannelParams(){
        try{

            Log.d("curr channel id:%s; channelParams:%s", currChannelID, currChannelParams);
            UChannel c = this.channelManager.getChannel(this.currChannelID);
            if(c == null){
                renderState(false);
                return;
            }

            if(StringUtils.isEmpty(currChannelParams)){
                renderState(false);
                return;
            }

            c.setChannelParams(currChannelParams);
            channelManager.saveChannel(c);
            renderState(true);
            return;

        }catch (Exception e){
            renderState(false);
            e.printStackTrace();
        }
    }


    private void renderState(boolean suc){
        JSONObject json = new JSONObject();
        json.put("state", suc? 1 : 0);
        json.put("msg", suc? "操作成功" : "操作失败");
        renderText(json.toString());
    }

    private void renderState(boolean suc, String msg){
        JSONObject json = new JSONObject();
        json.put("state", suc? 1 : 0);
        json.put("msg", msg);
        renderText(json.toString());
    }


    @Override
    public UChannel getModel() {

        if(this.channel == null){
            this.channel = new UChannel();
        }

        return this.channel;
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

    public UChannel getChannel() {
        return channel;
    }

    public void setChannel(UChannel channel) {
        this.channel = channel;
    }

    public int getCurrChannelID() {
        return currChannelID;
    }

    public void setCurrChannelID(int currChannelID) {
        this.currChannelID = currChannelID;
    }

    public String getSearchMaserName() {
        return searchMaserName;
    }

    public void setSearchMaserName(String searchMaserName) {
        this.searchMaserName = searchMaserName;
    }

    public String getSearchGameName() {
        return searchGameName;
    }

    public void setSearchGameName(String searchGameName) {
        this.searchGameName = searchGameName;
    }

    public int getCurrAppID() {
        return currAppID;
    }

    public void setCurrAppID(int currAppID) {
        this.currAppID = currAppID;
    }

    public String getCurrChannelParams() {
        return currChannelParams;
    }

    public void setCurrChannelParams(String currChannelParams) {
        this.currChannelParams = currChannelParams;
    }
}
