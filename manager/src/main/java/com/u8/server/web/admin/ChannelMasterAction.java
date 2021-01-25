package com.u8.server.web.admin;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.common.Page;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UChannelMaster;
import com.u8.server.log.Log;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.UChannelMasterManager;
import com.u8.server.service.UChannelParamMetaManager;
import com.u8.server.utils.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 渠道商管理
 * Created by ant on 2015/8/22.
 */
@Controller
@Namespace("/admin/channelMaster")
public class ChannelMasterAction extends UActionSupport implements ModelDriven<UChannelMaster> {

    private int page;           //当前请求的页码
    private int rows;           //当前每页显示的行数

    private UChannelMaster master;

    private int currMasterID;

    @Autowired
    private UChannelMasterManager channelMasterManager;

    @Autowired
    private UChannelManager channelManager;

    @Autowired
    private UChannelParamMetaManager channelParamMetaManager;


    @Action(value = "showChannelMasters",
            results = {@Result(name = "success", location = "/WEB-INF/admin/channelMaster.jsp")})
    public String showChannelMasters(){

        return "success";
    }

    @Action("getAllMastersSimple")
    public void getAllChannelMastersSimple(){
        try{

            List<UChannelMaster> masters = this.channelMasterManager.queryAll();

            JSONArray masterArray = new JSONArray();
            for(UChannelMaster m : masters){
                JSONObject item = new JSONObject();
                item.put("masterID", m.getMasterID());
                item.put("masterName", m.getMasterName());
                masterArray.add(item);
            }

            renderJson(masterArray.toString());

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Action("getAllChannelMasters")
    public void getAllChannelMasters(){

        try {

            Page<UChannelMaster> currPage = channelMasterManager.queryPage(page, rows, master.getMasterID(), master.getSdkName(), master.getMasterName() ,master.getNameSuffix(), master.getVerifyClass());
            JSONObject json = new JSONObject();

            json.put("total", currPage == null ? 0 : currPage.getTotalCount());
            JSONArray users = new JSONArray();
            if(currPage != null){
                for(UChannelMaster m : currPage.getResultList()){
                    users.add(m.toJSON());
                }
            }

            json.put("rows", users);

            renderJson(json.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //添加或者编辑
    @Action("saveMaster")
    public void saveChannelMasters(){

        try{

            if(StringUtils.isEmpty(this.master.getSdkName())){
                Log.e("sdkName can not be empty");
                renderState(false);
                return;
            }

            Log.d("save.master.info."+this.master.toJSON().toString());
            if(this.master.getMasterID() == null || this.master.getMasterID() ==0){
                //新增
                UChannelMaster exist = channelMasterManager.getMasterBySDKName(this.master.getSdkName());

                if(exist != null){

                    Log.e("already exist a master with same sdkName");
                    renderState(false);
                    return;
                }

            }else{
                //编辑
                UChannelMaster master = channelMasterManager.getChannelMaster(this.master.getMasterID());
                if(master == null){
                    Log.e("master not exist with id:"+this.master.getMasterID());
                    renderState(false);
                    return;
                }

//                UChannelMaster exist = channelMasterManager.getMasterBySDKName(this.master.getSdkName());
//                if(exist != null && !exist.getMasterID().equals(master.getMasterID())){
//                    Log.e("already exist a master with same sdkName");
//                    renderState(false);
//                    return;
//                }


            }
            channelMasterManager.saveChannelMaster(master);
            renderState(true);

            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false);
    }

    @Action("removeMaster")
    public void removeChannelMaster(){
        try{

            Log.d("Curr masterID is "+this.currMasterID);
            UChannelMaster m = this.channelMasterManager.getChannelMaster(this.currMasterID);
            if(m == null){
                renderState(false);
                return;
            }

            long count = this.channelManager.getChannelCountByMaster(this.currMasterID);

            if(count > 0){
                renderState(false, "请先删除该渠道商下面的所有渠道数据");
                return;
            }

            this.channelMasterManager.deleteChannelMaster(m);

            renderState(true);
            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false);
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

    public UChannelMaster getMaster() {
        return master;
    }

    public void setMaster(UChannelMaster master) {
        this.master = master;
    }

    @Override
    public UChannelMaster getModel() {

        if(this.master == null){
            this.master = new UChannelMaster();
        }
        return this.master;
    }

    public int getCurrMasterID() {
        return currMasterID;
    }

    public void setCurrMasterID(int currMasterID) {
        this.currMasterID = currMasterID;
    }
}
