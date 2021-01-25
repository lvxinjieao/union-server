package com.u8.server.web.admin;

import com.u8.server.common.Page;
import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UAdmin;
import com.u8.server.data.UGame;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UAdminManager;
import com.u8.server.service.UGameManager;
import com.u8.server.service.UOrderManager;
import com.u8.server.utils.TimeUtils;
import jxl.SheetSettings;
import jxl.Workbook;
import jxl.format.Alignment;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 订单查询
 * Created by ant on 2016/8/5.
 */
@Namespace("/admin/orders")
public class OrderSearchAction extends UActionSupport {

    private int page;               //当前请求的页码
    private int rows;               //当前每页显示的行数

    private Long    orderID;        //订单号
    private Integer appID;          //当前所属游戏ID
    private Integer channelID;      //当前所属渠道ID
    private Integer subChannelID;   //子渠道ID
    private Integer userID;         //U8Server这边对应的用户ID
    private String  username;       //U8Server这边生成的用户名
    private String  productID;      //游戏中商品ID
    private String  productName;    //游戏中商品名称

    private Integer minMoney;       //单位 分, 下单时收到的金额，实际充值的金额以这个为准
    private Integer maxMoney;
    private Integer minRealMoney;   //单位 分，渠道SDK支付回调通知返回的金额，记录，留作查账
    private Integer maxRealMoney;

    private String  roleID;         //游戏中角色ID
    private String  roleName;       //游戏中角色名称
    private String  serverID;       //服务器ID
    private Integer state;          //订单状态
    private String  channelOrderID;  //渠道SDK对应的订单号

    private String extension;       //根据extension查询

    private Date    beginCreateTime;       //订单创建时间
    private Date    endCreateTime;


    @Autowired
    private UOrderManager orderManager;

    @Autowired
    private UAdminManager adminManager;

    @Autowired
    private UGameManager gameManager;

    @Action("searchOrders")
    public void searchOrders(){
        try{
            UAdmin admin = (UAdmin)session.get("admin");

            Page<UOrder> currPage = this.orderManager.queryPage(page, rows, orderID, appID, channelID, subChannelID, userID, username, productID
                    ,productName, minMoney, maxMoney, minRealMoney, maxRealMoney, roleID, roleName, serverID, state
                    , channelOrderID, extension, beginCreateTime, endCreateTime, adminManager.getPermissonedGameIDs(admin));

            JSONObject json = new JSONObject();
            json.put("total", currPage == null ? 0 : currPage.getTotalCount());
            JSONArray users = new JSONArray();
            if(currPage != null){
                for(UOrder m : currPage.getResultList()){
                    users.add(m.toJSON());
                }
            }

            json.put("rows", users);

            renderJson(json.toString());

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Action("exportOrders")
    public void exportData(){
        try{
            UAdmin admin = (UAdmin)session.get("admin");
            page = 1;
            rows = Integer.MAX_VALUE;

            Page<UOrder> currPage = this.orderManager.queryPage(page, rows, orderID, appID, channelID, subChannelID, userID, username, productID
                    ,productName, minMoney, maxMoney, minRealMoney, maxRealMoney, roleID, roleName, serverID, state
                    , channelOrderID, extension, beginCreateTime, endCreateTime, adminManager.getPermissonedGameIDs(admin));

            String fileName = "order-" + TimeUtils.format(new Date(), TimeUtils.FORMATER_7) + ".xls";

            response.setContentType("application/x-excel");
            response.setCharacterEncoding("UTF-8");
            response.addHeader("Content-Disposition", "attachment;filename="
                    + fileName);// excel文件名

            List<UOrder> orders = currPage.getResultList();

            try {
                // 1.创建excel文件
                WritableWorkbook book = Workbook.createWorkbook(response
                        .getOutputStream());
                // 居中
                WritableCellFormat wf = new WritableCellFormat();
                wf.setAlignment(Alignment.CENTRE);

                WritableSheet sheet = book.createSheet("order", 0);
                SheetSettings settings = sheet.getSettings();
                settings.setVerticalFreeze(1);

                //添加第一行标题行

                sheet.addCell(new Label(0, 0, "订单号",wf));
                sheet.addCell(new Label(1, 0, "渠道号",wf));
                sheet.addCell(new Label(2, 0, "子渠道号",wf));
                sheet.addCell(new Label(3, 0, "用户ID",wf));
                sheet.addCell(new Label(4, 0, "商品ID",wf));
                sheet.addCell(new Label(5, 0, "商品名称",wf));
                sheet.addCell(new Label(6, 0, "商品描述",wf));
                sheet.addCell(new Label(7, 0, "金额",wf));
                sheet.addCell(new Label(8, 0, "实付金额",wf));
                sheet.addCell(new Label(9, 0, "角色ID",wf));
                sheet.addCell(new Label(10, 0, "角色名称",wf));
                sheet.addCell(new Label(11, 0, "服务器ID",wf));
                sheet.addCell(new Label(12, 0, "服务器名称",wf));
                sheet.addCell(new Label(13, 0, "状态",wf));
                sheet.addCell(new Label(14, 0, "渠道订单号",wf));
                sheet.addCell(new Label(15, 0, "扩展数据",wf));
                sheet.addCell(new Label(16, 0, "创建时间",wf));

                for (int i = 0; i < orders.size(); i++) {

                    UOrder s = orders.get(i);
                    sheet.addCell(new Label(0, i+1, s.getOrderID()+"",wf));
                    sheet.addCell(new Label(1, i+1, s.getChannelID()+"",wf));
                    sheet.addCell(new Label(2, i+1, s.getSubChannelID()+"",wf));
                    sheet.addCell(new Label(3, i+1, s.getUserID()+"",wf));
                    sheet.addCell(new Label(4, i+1, s.getProductID(),wf));
                    sheet.addCell(new Label(5, i+1, s.getProductName(),wf));
                    sheet.addCell(new Label(6, i+1, s.getProductDesc(),wf));
                    sheet.addCell(new Label(7, i+1, s.getMoney()/100 + "",wf));
                    sheet.addCell(new Label(8, i+1, s.getRealMoney() == null ? "0" : s.getRealMoney()/100+"",wf));
                    sheet.addCell(new Label(9, i+1, s.getRoleID(),wf));
                    sheet.addCell(new Label(10, i+1, s.getRoleName(),wf));
                    sheet.addCell(new Label(11, i+1, s.getServerID(),wf));
                    sheet.addCell(new Label(12, i+1, s.getServerName(),wf));

                    String t = "未知";
                    switch (s.getState()){
                        case PayState.STATE_PAYING:
                            t = "等待支付";
                            break;
                        case PayState.STATE_SUC:
                            t = "支付成功";
                            break;
                        case PayState.STATE_COMPLETE:
                            t = "交易完成";
                            break;
                        case PayState.STATE_FAILED:
                            t = "交易失败";
                            break;
                    }

                    sheet.addCell(new Label(13, i+1, t,wf));
                    sheet.addCell(new Label(14, i+1, s.getChannelOrderID(),wf));
                    sheet.addCell(new Label(15, i+1, s.getExtension(),wf));
                    sheet.addCell(new Label(16, i+1, TimeUtils.format(s.getCreatedTime(), TimeUtils.FORMATER_3),wf));
                }

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

    @Action("queryOrders")
    public void queryOrders(){
        try{

            page = 1;
            rows = Integer.MAX_VALUE;

            List<UGame> games = gameManager.queryAllGames(null);

            if(games == null || games.size() == 0){
                Log.e("there is no game");
                return;
            }

            List<Integer> gameIds = new ArrayList<Integer>();
            for(UGame game : games){
                gameIds.add(game.getAppID());
            }


            Page<UOrder> currPage = this.orderManager.queryPage(page, rows, orderID, appID, channelID, subChannelID, userID, username, productID
                    ,productName, minMoney, maxMoney, minRealMoney, maxRealMoney, roleID, roleName, serverID, state
                    , channelOrderID, extension, beginCreateTime, endCreateTime, gameIds.toArray());

            JSONObject json = new JSONObject();
            json.put("total", currPage == null ? 0 : currPage.getTotalCount());
            JSONArray users = new JSONArray();
            if(currPage != null){
                for(UOrder m : currPage.getResultList()){
                    users.add(m.toJSON());
                }
            }
            json.put("rows", users);
            renderJson(json.toString());
        }catch(Exception e){
            e.printStackTrace();
        }
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

    public Long getOrderID() {
        return orderID;
    }

    public void setOrderID(Long orderID) {
        this.orderID = orderID;
    }

    public Integer getAppID() {
        return appID;
    }

    public void setAppID(Integer appID) {
        this.appID = appID;
    }

    public Integer getChannelID() {
        return channelID;
    }

    public void setChannelID(Integer channelID) {
        this.channelID = channelID;
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public Integer getMinMoney() {
        return minMoney;
    }

    public void setMinMoney(Integer minMoney) {
        this.minMoney = minMoney;
    }

    public Integer getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(Integer maxMoney) {
        this.maxMoney = maxMoney;
    }

    public Integer getMinRealMoney() {
        return minRealMoney;
    }

    public void setMinRealMoney(Integer minRealMoney) {
        this.minRealMoney = minRealMoney;
    }

    public Integer getMaxRealMoney() {
        return maxRealMoney;
    }

    public void setMaxRealMoney(Integer maxRealMoney) {
        this.maxRealMoney = maxRealMoney;
    }

    public String getRoleID() {
        return roleID;
    }

    public void setRoleID(String roleID) {
        this.roleID = roleID;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getServerID() {
        return serverID;
    }

    public void setServerID(String serverID) {
        this.serverID = serverID;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }

    public String getChannelOrderID() {
        return channelOrderID;
    }

    public void setChannelOrderID(String channelOrderID) {
        this.channelOrderID = channelOrderID;
    }

    public Date getBeginCreateTime() {
        return beginCreateTime;
    }

    public void setBeginCreateTime(Date beginCreateTime) {
        this.beginCreateTime = beginCreateTime;
    }

    public Date getEndCreateTime() {
        return endCreateTime;
    }

    public void setEndCreateTime(Date endCreateTime) {
        this.endCreateTime = endCreateTime;
    }

    public Integer getSubChannelID() {
        return subChannelID;
    }

    public void setSubChannelID(Integer subChannelID) {
        this.subChannelID = subChannelID;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }
}
