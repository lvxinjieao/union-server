package com.u8.server.web.admin;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.common.Page;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UCPInfo;
import com.u8.server.log.Log;
import com.u8.server.service.UCPInfoManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * CP信息管理
 * Created by ant on 2017/8/31.
 */

@Controller
@Namespace("/admin/cps")
public class CPInfoAction extends UActionSupport implements ModelDriven<UCPInfo> {

    private int page;           //当前请求的页码
    private int rows;           //当前每页显示的行数

    private UCPInfo cpinfo;

    private Integer cpID;

    @Autowired
    private UCPInfoManager cpinfoManager;

    @Override
    public UCPInfo getModel() {
        if(cpinfo == null){
            cpinfo = new UCPInfo();
        }
        return cpinfo;
    }

    @Action(value = "showCPs", results = {@Result(name = "success", location = "/WEB-INF/admin/cpinfos.jsp")})
    public String cpManage(){

        return "success";
    }

    @Action("getAllCPs")
    public void getAllCPInfos(){
        try{

            Page<UCPInfo> currPage = cpinfoManager.getCPInfoPage(page, rows, cpinfo.getId(), cpinfo.getName());
            JSONObject json = new JSONObject();

            json.put("total", currPage == null ? 0 : currPage.getTotalCount());
            JSONArray users = new JSONArray();
            if(currPage != null){
                for(UCPInfo m : currPage.getResultList()){
                    users.add(m.toJSON());
                }
            }

            json.put("rows", users);

            renderJson(json.toString());


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Action("getAllCPsSimple")
    public void getAllCPInfosSimple(){
        try{
            List<UCPInfo> games = this.cpinfoManager.getAllCPList();

            JSONArray gameArray = new JSONArray();
            for(UCPInfo game : games){
                JSONObject item = new JSONObject();
                item.put("cpID", game.getId());
                item.put("name", game.getName());
                gameArray.add(item);
            }

            renderJson(gameArray.toString());

        }catch(Exception e){
            e.printStackTrace();
        }
    }


    //添加或者编辑
    @Action("saveCP")
    public void saveCP(){

        try{

            Log.d("save.cp.info." + this.cpinfo.toJSON().toString());

            if(cpinfo.getId() != null && cpinfo.getId() > 0){
                //编辑

                UCPInfo exists = cpinfoManager.getCPByID(cpinfo.getId());
                if(exists == null){

                    return;
                }
                exists.copyFrom(this.cpinfo);
                cpinfoManager.updateCP(exists);
            }else{
                //添加
                cpinfoManager.addCP(cpinfo);
            }

            renderState(true);
            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false);
    }

    @Action("removeCP")
    public void removeCP(){
        try{

            Log.d("Curr cpID is "+this.cpID);

            UCPInfo m = this.cpinfoManager.getCPByID(this.cpID);
            if(m == null){
                renderState(false);
                return;
            }


            this.cpinfoManager.deleteCP(m);

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

    public UCPInfo getCpinfo() {
        return cpinfo;
    }

    public void setCpinfo(UCPInfo cpinfo) {
        this.cpinfo = cpinfo;
    }

    public Integer getCpID() {
        return cpID;
    }

    public void setCpID(Integer cpID) {
        this.cpID = cpID;
    }
}
