package com.u8.server.web.admin;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.common.Page;
import com.u8.server.common.UActionSupport;
import com.u8.server.constants.PayState;
import com.u8.server.data.UOrder;
import com.u8.server.log.Log;
import com.u8.server.service.UOrderManager;
import com.u8.server.task.OrderTaskManager;
import com.u8.server.web.pay.SendAgent;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 订单管理
 * Created by ant on 2015/8/29.
 */
@Controller
@Namespace("/admin/orders")
public class OrderManAction extends UActionSupport implements ModelDriven<UOrder> {

    private int page;           //当前请求的页码
    private int rows;           //当前每页显示的行数

    private UOrder order;

    private long currOrderID;

    @Autowired
    private UOrderManager orderManager;

    @Action(value = "showOrders", results = {@Result(name = "success", location = "/WEB-INF/admin/orders.jsp")})
    public String showOrders() {
        return "success";
    }

    @Action(value = "showOrderAnalytics", results = {@Result(name = "success", location = "/WEB-INF/admin/order_analytics.jsp")})
    public String showOrderAnalytics() {
        return "success";
    }


    @Action("getAllOrders")
    public void getAllOrders() {
        try {
            Page<UOrder> currPage = this.orderManager.queryPage(page, rows);
            JSONObject json = new JSONObject();
            json.put("total", currPage.getTotalCount());
            JSONArray orders = new JSONArray();
            if (currPage != null) {
                for (UOrder m : currPage.getResultList()) {
                    orders.add(m.toJSON());
                }
            }
            json.put("rows", orders);
            renderJson(json.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /***
     * 补单，只有状态为处理成功的订单
     */
    @Action("resendOrder")
    public void resendOrder() {
        try {

            Log.d("补单订单号：" + this.currOrderID);
            UOrder order = orderManager.getOrder(this.currOrderID);
            if (order == null) {
                Log.e("订单号不存咋：" + this.currOrderID);
                renderState(false);
                return;
            }

            if (order.getState() != PayState.STATE_SUC) {
                Log.e("不能重复补单  状态不是STATE_SUC 当前状态：" + order.getState());
                renderState(false);
                return;
            }
            SendAgent.resendCallbackToServer(this.orderManager, order);
            renderState(true);
        } catch (Exception e) {
            e.printStackTrace();
            renderState(false);
        }
    }

    /***
     * 补单，只有状态为处理成功的订单
     */
    @Action("resendOrders")
    public void resendOrders() {
        try {

            Log.d("补单订单号：" + this.currOrderID);
            List<UOrder> orders = orderManager.getOrdersWithState(PayState.STATE_SUC);

            if (orders == null || orders.size() == 0) {
                Log.d("there is no order need to resend");
                renderState(true, "没有任何需要补发的订单");
                return;
            }
            int size = orders.size();
            for (UOrder order : orders) {
                OrderTaskManager.getInstance().addOrder(order);
            }
            renderState(true, "共补单" + size + "条记录，请稍后刷新界面查看订单状态变化");
        } catch (Exception e) {
            e.printStackTrace();
            renderState(false);
        }
    }

    @Action("removeOrder")
    public void removeOrder() {
        try {
            Log.d("Curr orderID is " + this.currOrderID);
            UOrder order = orderManager.getOrder(this.currOrderID);

            if (order == null) {
                renderState(false);
                return;
            }
            orderManager.deleteOrder(order);
            renderState(true);
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        renderState(false);
    }


    private void renderState(boolean suc) {
        JSONObject json = new JSONObject();
        json.put("state", suc ? 1 : 0);
        json.put("msg", suc ? "操作成功" : "操作失败");
        renderJson(json.toString());
    }

    private void renderState(boolean suc, String msg) {
        JSONObject json = new JSONObject();
        json.put("state", suc ? 1 : 0);
        json.put("msg", msg);
        renderJson(json.toString());
    }

    @Override
    public UOrder getModel() {
        if (this.order == null) {
            this.order = new UOrder();
        }
        return this.order;
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

    public UOrder getOrder() {
        return order;
    }

    public void setOrder(UOrder order) {
        this.order = order;
    }

    public long getCurrOrderID() {
        return currOrderID;
    }

    public void setCurrOrderID(long currOrderID) {
        this.currOrderID = currOrderID;
    }
}
