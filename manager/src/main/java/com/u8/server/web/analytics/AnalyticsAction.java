package com.u8.server.web.analytics;

import com.u8.server.common.Page;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UChannel;
import com.u8.server.data.UChannelMaster;
import com.u8.server.data.UGame;
import com.u8.server.data.analytics.TChannelSummary;
import com.u8.server.data.analytics.TRetention;
import com.u8.server.data.analytics.TSummary;
import com.u8.server.log.Log;
import com.u8.server.service.UChannelManager;
import com.u8.server.service.UChannelMasterManager;
import com.u8.server.service.UGameManager;
import com.u8.server.service.analytics.AnalyticsManager;
import com.u8.server.utils.GUtils;
import com.u8.server.utils.TimeUtils;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.Date;
import java.util.List;

/**
 * 统计分析
 * Created by ant on 2016/8/25.
 */
@Controller
@Namespace("/analytics")
public class AnalyticsAction extends UActionSupport{

    private int page;           //当前请求的页码
    private int rows;           //当前每页显示的行数

    private Integer appID;
    private Integer channelID;
    private Integer timeType;

    private Date beginTime;
    private Date endTime;

    @Autowired
    private AnalyticsManager summaryManager;

    @Autowired
    private UGameManager gameManager;

    @Autowired
    private UChannelManager channelManager;

    @Autowired
    private UChannelMasterManager channelMasterManager;

    @Action(value = "summary", results = {@Result(name = "success", location = "/WEB-INF/admin/t_summary.jsp")})
    public String showSummary(){
        return "success";
    }

    @Action(value = "newUsers", results = {@Result(name = "success", location = "/WEB-INF/admin/t_newuser.jsp")})
    public String showNewUsers(){

        return "success";
    }

    @Action(value = "dau", results = {@Result(name = "success", location = "/WEB-INF/admin/t_dau.jsp")})
    public String showDAUData(){

        return "success";
    }

    @Action(value = "retention", results = {@Result(name = "success", location = "/WEB-INF/admin/t_retention.jsp")})
    public String showRetentionData(){

        return "success";
    }

    @Action(value = "flow", results = {@Result(name = "success", location = "/WEB-INF/admin/t_flow.jsp")})
    public String showFlowData(){

        return "success";
    }

    @Action(value = "money", results = {@Result(name = "success", location = "/WEB-INF/admin/t_money.jsp")})
    public String showMoneyData(){

        return "success";
    }

    @Action(value = "pay", results = {@Result(name = "success", location = "/WEB-INF/admin/t_pay.jsp")})
    public String showPayData(){

        return "success";
    }

    @Action(value = "payratio", results = {@Result(name = "success", location = "/WEB-INF/admin/t_payratio.jsp")})
    public String showPayRatioData(){

        return "success";
    }

    @Action(value = "gameSummaryData", results = {@Result(name = "success", location = "/WEB-INF/admin/t_gameData.jsp")})
    public String showGameSummaryData(){
        return "success";
    }


    @Action("todaySummaryData")
    public void getTodaySummaryData() {
        try{

            String json = null;
            if(channelID != null && channelID > 0){
                json = summaryManager.collectTodayInfoForChannel(channelID);
            }else{
                json = summaryManager.collectTodayInfoForGame(appID);
            }

            renderState(true, json);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Action("summaryData")
    public void getSummaryData(){
        try{

            Date from = null;
            Date to = TimeUtils.dateBegin(new Date());

            switch (timeType){
                case 1:
                    Date lastDay = TimeUtils.lastDay();
                    from = TimeUtils.dateBegin(lastDay);
                    break;
                case 2:
                    Date lastWeek = TimeUtils.dateSub(new Date(), 7);
                    from = TimeUtils.dateBegin(lastWeek);
                    break;
                case 3:
                    Date lastMonth = TimeUtils.dateSub(new Date(), 30);
                    from = TimeUtils.dateBegin(lastMonth);
                    break;


            }

            String json = summaryManager.collectSummaryInfo(appID, from, to);

            renderState(true, json);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Action("newUserData")
    public void getNewUserData(){
        try{

            if(this.beginTime == null || this.endTime == null){
                renderState(false, "请指定时间段");
                return;
            }

            String json = summaryManager.collectNewUserInfo(appID, this.beginTime, this.endTime);

            Log.d("the json data is ");
            Log.d(json);

            renderState(true, json);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Action("dauData")
    public void getDAUData(){
        try{

            if(this.beginTime == null || this.endTime == null){
                renderState(false, "请指定时间段");
                return;
            }

            String json = summaryManager.collectDAUInfo(appID, this.beginTime, this.endTime);

            Log.d("the json data is ");
            Log.d(json);

            renderState(true, json);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Action("retentionData")
    public void getRetentionData(){
        try{

            if(this.beginTime == null || this.endTime == null){
                renderState(false, "请指定时间段");
                return;
            }

            String json = summaryManager.collectRetentionInfo(appID, this.beginTime, this.endTime);

            Log.d("the json data is ");
            Log.d(json);

            renderState(true, json);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Action("flowData")
    public void getFlowData(){
        try{

            if(this.beginTime == null || this.endTime == null){
                renderState(false, "请指定时间段");
                return;
            }



            String json = summaryManager.collectFlowInfo(appID, this.beginTime, this.endTime, this.timeType);

            Log.d("the json data is ");
            Log.d(json);

            renderState(true, json);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Action("moneyData")
    public void getMoneyData(){
        try{

            if(this.beginTime == null || this.endTime == null){
                renderState(false, "请指定时间段");
                return;
            }

            String json = summaryManager.collectMoneyInfo(appID, this.beginTime, this.endTime);

            Log.d("the json data is ");
            Log.d(json);

            renderState(true, json);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Action("payData")
    public void getPayData(){
        try{

            if(this.beginTime == null || this.endTime == null){
                renderState(false, "请指定时间段");
                return;
            }

            String json = summaryManager.collectPayInfo(appID, this.beginTime, this.endTime);

            Log.d("the json data is ");
            Log.d(json);

            renderState(true, json);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Action("payRatioData")
    public void getPayRatioData(){
        try{

            if(this.beginTime == null || this.endTime == null){
                renderState(false, "请指定时间段");
                return;
            }

            String json = summaryManager.collectPayRatioInfo(appID, this.beginTime, this.endTime);

            Log.d("the json data is ");
            Log.d(json);

            renderState(true, json);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Action("getGameSummaryData")
    public void getGameSummaryData(){
        try{

            Page<TSummary> pages = summaryManager.getSummaryPage(page, rows, appID, beginTime, endTime);

            JSONObject json = new JSONObject();

            JSONArray users = new JSONArray();
            if(pages != null){
                json.put("total", pages.getTotalCount());
                for(TSummary m : pages.getResultList()){
                    users.add(m.toJSON());
                }
            }

            json.put("rows", users);

            renderJson(json.toString());


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Action("exportGameSummaryData")
    public void exportGameSummaryData(){

        try{

            UGame game = this.gameManager.getGame(this.appID);
            if(game == null){
                renderState(false, null);
                return;
            }

            List<TSummary> lst = summaryManager.getSummaryList(appID, beginTime, endTime);
            List<TRetention> rlst = summaryManager.getRetentionList(appID, beginTime, endTime);

            String fileName = appID + "-" + TimeUtils.format(new Date(), TimeUtils.FORMATER_7) + ".xls";

            response.setContentType("application/x-excel");
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename="
                    + fileName);// excel文件名


            try {
                // 1.创建excel文件
                WritableWorkbook book = Workbook.createWorkbook(response
                        .getOutputStream());
                // 居中
                WritableCellFormat wf = new WritableCellFormat();
                wf.setAlignment(Alignment.CENTRE);

                WritableSheet sheet = book.createSheet(game.getName(), 0);
                SheetSettings settings = sheet.getSettings();
                settings.setVerticalFreeze(1);

                //添加第一行标题行

                sheet.addCell(new Label(0, 0, "日期",wf));
                sheet.addCell(new Label(1, 0, "ID",wf));
                sheet.addCell(new Label(2, 0, "游戏ID",wf));
                sheet.addCell(new Label(3, 0, "新增设备",wf));
                sheet.addCell(new Label(4, 0, "新增用户",wf));
                sheet.addCell(new Label(5, 0, "新增用户(去重)",wf));
                sheet.addCell(new Label(6, 0, "总用户",wf));
                sheet.addCell(new Label(7, 0, "活跃用户",wf));
                sheet.addCell(new Label(8, 0, "活跃用户(新)",wf));
                sheet.addCell(new Label(9, 0, "7日活跃",wf));
                sheet.addCell(new Label(10, 0, "30日活跃",wf));
                sheet.addCell(new Label(11, 0, "平均在线时长",wf));
                sheet.addCell(new Label(12, 0, "付费用户",wf));
                sheet.addCell(new Label(13, 0, "总付费用户",wf));
                sheet.addCell(new Label(14, 0, "付费用户(新)",wf));
                sheet.addCell(new Label(15, 0, "收入(元)",wf));
                sheet.addCell(new Label(16, 0, "次日留存",wf));
                sheet.addCell(new Label(17, 0, "3日留存",wf));
                sheet.addCell(new Label(18, 0, "7日留存",wf));
                sheet.addCell(new Label(19, 0, "15日留存",wf));
                sheet.addCell(new Label(20, 0, "30日留存",wf));
                sheet.addCell(new Label(21, 0, "次日付费转化",wf));
                sheet.addCell(new Label(22, 0, "3日付费转化",wf));
                sheet.addCell(new Label(23, 0, "7日付费转化",wf));
                sheet.addCell(new Label(24, 0, "15日付费转化",wf));
                sheet.addCell(new Label(25, 0, "30日付费转化",wf));
                sheet.addCell(new Label(26, 0, "7日流失率",wf));
                sheet.addCell(new Label(27, 0, "14日流失率",wf));
                sheet.addCell(new Label(28, 0, "30日流失率",wf));
                sheet.addCell(new Label(29, 0, "7日回归率",wf));
                sheet.addCell(new Label(30, 0, "14日回归率",wf));
                sheet.addCell(new Label(31, 0, "30日回归率",wf));

                for (int i = 0; i < lst.size(); i++) {
                    // 3.添加第一行及第二行标题数据
                    TSummary s = lst.get(i);
                    sheet.addCell(new Label(0, i+1, TimeUtils.format(s.getCurrTime(), TimeUtils.FORMATER_7),wf));
                    sheet.addCell(new Label(1, i+1, s.getId()+"",wf));
                    sheet.addCell(new Label(2, i+1, s.getAppID()+"("+game.getName()+")",wf));
                    sheet.addCell(new Label(3, i+1, s.getDeviceNum()+"",wf));
                    sheet.addCell(new Label(4, i+1, s.getUserNum()+"",wf));
                    sheet.addCell(new Label(5, i+1, s.getUniUserNum()+"",wf));
                    sheet.addCell(new Label(6, i+1, s.getTotalUserNum()+"",wf));
                    sheet.addCell(new Label(7, i+1, s.getDau()+"",wf));
                    sheet.addCell(new Label(8, i+1, s.getNdau()+"",wf));
                    sheet.addCell(new Label(9, i+1, s.getWau()+"",wf));
                    sheet.addCell(new Label(10, i+1, s.getMau()+"",wf));
                    sheet.addCell(new Label(11, i+1, s.getAvg()+"",wf));
                    sheet.addCell(new Label(12, i+1, s.getPayUserNum()+"",wf));
                    sheet.addCell(new Label(13, i+1, s.getTotalPayUserNum()+"",wf));
                    sheet.addCell(new Label(14, i+1, s.getNewPayUserNum()+"",wf));
                    sheet.addCell(new Label(15, i+1, String.format("%.2f", (s.getMoney()/100f)),wf));

                    //率信息
                    if(i < rlst.size()){

                        TRetention r = rlst.get(i);


                        //留存信息解析
                        if(r.getDayRetention() == null){
                            r.setDayRetention(",");
                        }

                        String rs = r.getDayRetention();
                        if(rs.startsWith(",")){
                            rs = rs.substring(1);
                        }
                        rs = rs.trim();

                        String stay1 = "0";
                        String stay3 = "0";
                        String stay7 = "0";
                        String stay15 = "0";
                        String stay30 = "0";
                        String[] userNums = rs.split(",");
                        if(userNums.length >= 1){
                            String[] vals = userNums[0].split(":");
                            float v = (vals[0].length() <= 1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                            stay1 = String.format("%.2f", v);
                        }

                        if(userNums.length >= 3){
                            String[] vals = userNums[2].split(":");

                            float v = (vals[0].length() <=1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                            stay3 = String.format("%.2f", v);
                        }

                        if(userNums.length >= 7){
                            String[] vals = userNums[6].split(":");

                            float v = (vals[0].length() <=1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                            stay7 = String.format("%.2f", v);
                        }

                        if(userNums.length >= 15){
                            String[] vals = userNums[14].split(":");

                            float v = (vals[0].length() <=1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                            stay15 = String.format("%.2f", v);
                        }

                        sheet.addCell(new Label(16, i+1, stay1,wf));
                        sheet.addCell(new Label(17, i+1, stay3,wf));
                        sheet.addCell(new Label(18, i+1, stay7,wf));
                        sheet.addCell(new Label(19, i+1, stay15,wf));
                        sheet.addCell(new Label(20, i+1, stay30,wf));

                        //end 留存信息解析

                        //付费转化解析
                        if (r.getDayPayRatio() == null) {
                            r.setDayPayRatio(",");
                        }

                        String pr = r.getDayPayRatio();
                        if (pr.startsWith(",")) {
                            pr = pr.substring(1);
                        }
                        pr = pr.trim();
                        String payRatio1 = "0";
                        String payRatio3 = "0";
                        String payRatio7 = "0";
                        String payRatio15 = "0";
                        String payRatio30 = "0";

                        String[] payNums = pr.split(",");
                        if(payNums.length >= 1){
                            String[] vals = payNums[0].split(":");
                            float v = (vals[0].length() <= 1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                            payRatio1 = String.format("%.2f", v);
                        }
                        if(payNums.length >= 3){
                            String[] vals = payNums[2].split(":");
                            float v = (vals[0].length() <= 1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                            payRatio3 = String.format("%.2f", v);
                        }
                        if(payNums.length >= 7){
                            String[] vals = payNums[6].split(":");
                            float v = (vals[0].length() <= 1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                            payRatio7 = String.format("%.2f", v);
                        }
                        if(payNums.length >= 15){
                            String[] vals = payNums[14].split(":");
                            float v = (vals[0].length() <= 1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                            payRatio15 = String.format("%.2f", v);
                        }
                        if(payNums.length >= 30){
                            String[] vals = payNums[29].split(":");
                            float v = (vals[0].length() <= 1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                            payRatio30 = String.format("%.2f", v);
                        }

                        sheet.addCell(new Label(21, i+1, payRatio1,wf));
                        sheet.addCell(new Label(22, i+1, payRatio3,wf));
                        sheet.addCell(new Label(23, i+1, payRatio7,wf));
                        sheet.addCell(new Label(24, i+1, payRatio15,wf));
                        sheet.addCell(new Label(25, i+1, payRatio30,wf));

                        //end 付费转化率

                        //流失率
                        if (r.getDayFlowRatio() == null) {
                            r.setDayFlowRatio(",");
                        }

                        String fr = r.getDayFlowRatio();
                        if (fr.startsWith(",")) {
                            fr = fr.substring(1);
                        }
                        fr = fr.trim();
                        String[] flowNums = fr.split(",");
                        String flowRatio7 = "0";
                        String flowRatio15 = "0";
                        String flowRatio30 = "0";

                        if(flowNums.length >= 1){
                            String[] vals = flowNums[0].split(":");
                            float v = (vals[0].length() <=1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                            flowRatio7 =  String.format("%.2f", v);
                        }

                        if(flowNums.length >= 2){
                            String[] vals = flowNums[1].split(":");
                            float v = (vals[0].length() <=1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                            flowRatio15 =  String.format("%.2f", v);
                        }

                        if(flowNums.length >= 3){
                            String[] vals = flowNums[2].split(":");
                            float v = (vals[0].length() <=1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                            flowRatio30 =  String.format("%.2f", v);
                        }

                        sheet.addCell(new Label(26, i+1, flowRatio7,wf));
                        sheet.addCell(new Label(27, i+1, flowRatio15,wf));
                        sheet.addCell(new Label(28, i+1, flowRatio30,wf));

                        //end 流失率

                        //回归率
                        if (r.getDayBackRatio() == null) {
                            r.setDayBackRatio(",");
                        }

                        String br = r.getDayBackRatio();
                        if (br.startsWith(",")) {
                            br = br.substring(1);
                        }
                        br = br.trim();
                        String[] backNums = br.split(",");
                        String backNum7 = "0";
                        String backNum15 = "0";
                        String backNum30 = "0";

                        if(backNums.length >= 1){
                            String[] vals = backNums[0].split(":");
                            float v = (vals[0].length() <=1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                            backNum7 =  String.format("%.2f", v);
                        }

                        if(backNums.length >= 2){
                            String[] vals = backNums[1].split(":");
                            float v = (vals[0].length() <=1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                            backNum15 =  String.format("%.2f", v);
                        }

                        if(backNums.length >= 3){
                            String[] vals = backNums[2].split(":");
                            float v = (vals[0].length() <=1 || Integer.valueOf(vals[1]) <= 0) ? 0 : Float.valueOf(vals[0])/Float.valueOf(vals[1]) * 100f;
                            backNum30 =  String.format("%.2f", v);
                        }

                        sheet.addCell(new Label(29, i+1, backNum7,wf));
                        sheet.addCell(new Label(30, i+1, backNum15,wf));
                        sheet.addCell(new Label(31, i+1, backNum30,wf));

                        //end 回归率

                    }


                }

                addChannelSheets(book, wf, game);

                // 6.写入excel并关闭
                book.write();
                book.close();
            } catch (Exception e) {
                e.printStackTrace();
            }


        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void addChannelSheets(WritableWorkbook book, WritableCellFormat wf, UGame game) throws WriteException {

        List<UChannel> channels = channelManager.getAllChannelsByAppID(appID);
        List<TChannelSummary> allSummaries = summaryManager.getChannelSummaryList(game.getAppID(), beginTime, endTime);
        List<UChannelMaster> allMasters = channelMasterManager.queryAll();


        if(channels != null && channels.size() > 0){

            for(int i=0; i<channels.size(); i++){
                final UChannel c = channels.get(i);

                String masterName = "";
                List<UChannelMaster> mLst = GUtils.subList(allMasters, new GUtils.ICompareFilter<UChannelMaster>() {
                    @Override
                    public boolean match(UChannelMaster data) {
                        return data.getMasterID() == c.getMasterID();
                    }
                });

                if(mLst != null && mLst.size() > 0){
                    masterName = mLst.get(0).getMasterName();
                }

                WritableSheet sheet = book.createSheet(masterName, i+1);
                SheetSettings settings = sheet.getSettings();
                settings.setVerticalFreeze(1);

                sheet.addCell(new Label(0, 0, "日期",wf));
                sheet.addCell(new Label(1, 0, "ID",wf));
                sheet.addCell(new Label(2, 0, "游戏ID",wf));
                sheet.addCell(new Label(3, 0, "渠道ID",wf));
                sheet.addCell(new Label(4, 0, "新增设备",wf));
                sheet.addCell(new Label(5, 0, "新增用户",wf));
                sheet.addCell(new Label(6, 0, "付费用户",wf));
                sheet.addCell(new Label(7, 0, "付费用户(新)",wf));
                sheet.addCell(new Label(8, 0, "收入(元)",wf));

                List<TChannelSummary> summaries = GUtils.subList(allSummaries, new GUtils.ICompareFilter<TChannelSummary>() {
                    @Override
                    public boolean match(TChannelSummary data) {
                        return data.getChannelID() == c.getChannelID();
                    }
                });

                for(int m = 0; m < summaries.size(); m++){

                    TChannelSummary s = summaries.get(m);
                    sheet.addCell(new Label(0, m+1, TimeUtils.format(s.getCurrTime(), TimeUtils.FORMATER_7),wf));
                    sheet.addCell(new Label(1, m+1, s.getId()+"",wf));
                    sheet.addCell(new Label(2, m+1, s.getAppID()+"("+game.getName()+")",wf));
                    sheet.addCell(new Label(3, m+1, c.getChannelID()+"",wf));
                    sheet.addCell(new Label(4, m+1, s.getDeviceNum()+"",wf));
                    sheet.addCell(new Label(5, m+1, s.getUserNum()+"",wf));
                    sheet.addCell(new Label(6, m+1, s.getPayUserNum()+"",wf));
                    sheet.addCell(new Label(7, m+1, s.getNewPayUserNum()+"",wf));
                    sheet.addCell(new Label(8, m+1, String.format("%.2f", (s.getMoney()/100f)),wf));

                }


            }

        }

    }

    private void renderState(boolean suc, String data){
        JSONObject json = new JSONObject();
        json.put("state", suc? 1 : 0);
        json.put("data", data);
        renderJson(json.toString());
    }

    public Integer getAppID() {
        return appID;
    }

    public void setAppID(Integer appID) {
        this.appID = appID;
    }

    public Integer getTimeType() {
        return timeType;
    }

    public void setTimeType(Integer timeType) {
        this.timeType = timeType;
    }

    public Date getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(Date beginTime) {
        this.beginTime = beginTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
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

    public Integer getChannelID() {
        return channelID;
    }

    public void setChannelID(Integer channelID) {
        this.channelID = channelID;
    }
}
