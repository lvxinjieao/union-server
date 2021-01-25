package com.u8.server.web.admin;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.common.Page;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UChannelParamMeta;
import com.u8.server.log.Log;
import com.u8.server.service.UChannelParamMetaManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Created by ant on 2018/2/22.
 */
@Controller
@Namespace("/admin/channelParamMeta")
public class ChannelParamMetaAction extends UActionSupport implements ModelDriven<UChannelParamMeta> {

    private int page;           //当前请求的页码
    private int rows;           //当前每页显示的行数

    private int currMasterID;       //当前渠道商ID
    private int currMetaID;         //当前meta ID

    private UChannelParamMeta meta;

    @Autowired
    private UChannelParamMetaManager channelParamMetaManager;

    @Override
    public UChannelParamMeta getModel() {

        if(meta == null){
            meta = new UChannelParamMeta();
        }


        return meta;
    }


    @Action(value = "showMetas",
            results = {@Result(name = "success", location = "/WEB-INF/admin/channelParamMeta.jsp")})
    public String showChannelMasters(){

        return "success";
    }

    @Action("getMetaList")
    public void getMetaList(){

        try {

            Page<UChannelParamMeta> currPage = channelParamMetaManager.queryPage(page, rows, currMasterID);
            JSONObject json = new JSONObject();

            json.put("total", currPage == null ? 0 : currPage.getTotalCount());
            JSONArray users = new JSONArray();
            if(currPage != null){
                for(UChannelParamMeta m : currPage.getResultList()){
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
    @Action("saveMeta")
    public void saveMeta(){

        try{

            Log.d("save.meta.info." + this.meta.toJSON().toString());
            channelParamMetaManager.saveMeta(meta);
            renderState(true);

            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false);
    }



    @Action("removeMeta")
    public void removeMeta(){
        try{

            Log.d("Curr meta id is "+this.currMasterID);

            UChannelParamMeta exists = this.channelParamMetaManager.getMetaByID(currMetaID);
            if(exists == null){
                renderState(false);
                return;
            }

            this.channelParamMetaManager.deleteMeta(exists);
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

    public UChannelParamMeta getMeta() {
        return meta;
    }

    public void setMeta(UChannelParamMeta meta) {
        this.meta = meta;
    }

    public int getCurrMasterID() {
        return currMasterID;
    }

    public void setCurrMasterID(int currMasterID) {
        this.currMasterID = currMasterID;
    }

    public int getCurrMetaID() {
        return currMetaID;
    }

    public void setCurrMetaID(int currMetaID) {
        this.currMetaID = currMetaID;
    }
}
