package com.u8.server.web.admin;

import com.opensymphony.xwork2.ModelDriven;
import com.u8.server.common.Page;
import com.u8.server.common.UActionSupport;
import com.u8.server.data.UChannelProduct;
import com.u8.server.log.Log;
import com.u8.server.service.UChannelProductManager;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

/**
 * Created by ant on 2018/5/15.
 */
@Controller
@Namespace("/admin/channelProduct")
public class ChannelProductAction extends UActionSupport implements ModelDriven<UChannelProduct> {

    private int page;           //当前请求的页码
    private int rows;           //当前每页显示的行数

    private UChannelProduct product;

    private Integer currProductID;
    private Integer currChannelID;

    @Autowired
    private UChannelProductManager productManager;

    @Action(value = "showProducts",
            results = {@Result(name = "success", location = "/WEB-INF/admin/channelProducts.jsp")})
    public String showChannelMasters(){

        return "success";
    }

    @Action("getProductList")
    public void getProductList(){

        try {

            Page<UChannelProduct> currPage = productManager.queryPage(page, rows, currChannelID);
            JSONObject json = new JSONObject();

            json.put("total", currPage == null ? 0 : currPage.getTotalCount());
            JSONArray users = new JSONArray();
            if(currPage != null){
                for(UChannelProduct m : currPage.getResultList()){
                    users.add(m.toJSON());
                }
            }

            json.put("rows", users);

            renderJson(json.toString());


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Action("saveProduct")
    public void saveProduct(){
        try{
            Log.d("save.Product.info." + this.product.toJSON().toString());

            if(this.product.getId() == null || this.product.getId() == 0){
                //新建
                UChannelProduct exist = productManager.getChannelProduct(this.product.getChannelID(), this.product.getGameProductID());
                if(exist != null){
                    Log.e("saveProduct failed. already exist a product with same gameProductID");
                    renderState(false);
                    return;
                }
            }else {
                //更改
                UChannelProduct existProduct = productManager.getProductByID(this.product.getId());
                if(existProduct == null){
                    Log.e("saveProduct failed. product not exist for product id:"+this.product.getId());
                    renderState(false);
                    return;
                }

                UChannelProduct exist = productManager.getChannelProduct(this.product.getChannelID(), this.product.getGameProductID());
                if(exist != null && !exist.getId().equals(product.getId())){
                    Log.e("saveProduct failed. already exist a product with same gameProductID");
                    renderState(false);
                    return;
                }

                existProduct.setChannelProductID(product.getChannelProductID());
                product = existProduct;
            }

            productManager.saveChannelProduct(product);
            renderState(true);

            return;

        }catch(Exception e){
            e.printStackTrace();
        }

        renderState(false);
    }

    @Action("deleteProduct")
    public void deleteProduct(){
        try{

            UChannelProduct product = productManager.getProductByID(currProductID);
            if(product == null){
                Log.e("product not exists for id:"+currProductID);
                renderState(false);
                return;
            }

            productManager.deleteChannelProduct(product);
            renderState(true);
            return;

        }catch (Exception e){
            e.printStackTrace();
            renderState(false);
        }
    }

    private void renderState(boolean suc){
        JSONObject json = new JSONObject();
        json.put("state", suc? 1 : 0);
        json.put("msg", suc? "操作成功" : "操作失败");
        renderText(json.toString());
    }

    @Override
    public UChannelProduct getModel() {
        if(product == null) product = new UChannelProduct();
        return product;
    }

    public UChannelProduct getProduct() {
        return product;
    }

    public void setProduct(UChannelProduct product) {
        this.product = product;
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

    public Integer getCurrChannelID() {
        return currChannelID;
    }

    public void setCurrChannelID(Integer currChannelID) {
        this.currChannelID = currChannelID;
    }

    public Integer getCurrProductID() {
        return currProductID;
    }

    public void setCurrProductID(Integer currProductID) {
        this.currProductID = currProductID;
    }
}
