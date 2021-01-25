package com.u8.server.web.api;

import com.u8.server.common.UActionSupport;
import com.u8.server.config.ManagerConfig;
import com.u8.server.data.*;
import com.u8.server.log.Log;
import com.u8.server.service.*;
import com.u8.server.service.analytics.AnalyticsManager;
import com.u8.server.utils.EncryptUtils;
import com.u8.server.utils.StringUtils;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.apache.struts2.convention.annotation.Action;
import org.apache.struts2.convention.annotation.Namespace;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import java.util.List;

/**
 * 打包工具交互相关的协议接口
 * Created by ant on 2018/4/4.
 */
@Controller
@Namespace("/api")
public class ApiAction extends UActionSupport {


    private String apiID;           //apiID
    private String excludeAppIDs;   //排除的appID，逗号分隔
    private String time;            //请求时间戳
    private String sign;            //签名

    private Integer appID;          //game id
    private Integer channelID;      //渠道号
    private String channelIDs;      //多个渠道号，逗号分割

    private String masterSdkName;   //渠道商唯一SDK标识

    private String channelParams;   //渠道参数
    private String products;        //商品json array

    //渠道商品映射关系
    private String productID;
    private String channelProductID;
    private String gameProductID;

    //渠道参数meta信息
    private String metaID;
    private String paramKey;
    private String showName;
    private String metaDesc;
    private String defaultVal;
    private String pos;
    private String paramType;
    private String paramStyle;
    private String styleExtra;

    @Autowired
    private ManagerConfig globalConfig;

    @Autowired
    private UGameManager gameManager;

    @Autowired
    private UChannelMasterManager masterManager;

    @Autowired
    private UChannelManager channelManager;

    @Autowired
    private UChannelParamMetaManager metaManager;

    @Autowired
    private UChannelProductManager productManager;

    @Autowired
    private AnalyticsManager analyticsManager;

    @Action("testAnalytic")
    public void testAnalytics() {
        renderJson(analyticsManager.collectTodayInfoForGame(1));
    }

    @Action("games")
    public void getAllGames() {
        try {

            if (StringUtils.isEmpty(apiID) || !apiID.equals(globalConfig.getApiID())) {
                renderFailed("ApiID错误");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("apiID=" + apiID).append("&excludeAppIDs=" + excludeAppIDs).append("&time=" + time).append(globalConfig.getApiKey());

            String signLocal = EncryptUtils.md5(sb.toString());

            Log.d("getAllGames: sign str:%s; sign local:%s;sign:%s", sb.toString(), signLocal, sign);
            if (!signLocal.equals(sign)) {
                renderFailed("签名错误");
                return;
            }

            String[] excludes = null;
            if (!StringUtils.isEmpty(excludeAppIDs)) {
                excludes = excludeAppIDs.split(",");
            }

            List<UGame> gameLst = gameManager.queryGamesExclude(excludes);

            JSONArray games = new JSONArray();
            for (UGame m : gameLst) {
                JSONObject item = new JSONObject();
                item.put("cpID", m.getCpID());
                item.put("appID", m.getAppID());
                item.put("appKey", m.getAppkey());
                item.put("appSecret", m.getAppSecret());
                item.put("name", m.getName());
                games.add(item);
            }
            renderSuc(games);
        } catch (Exception e) {
            renderFailed(e.getMessage());
            e.printStackTrace();
        }
    }

    @Action("channelmasters")
    public void getAllChannelMaster() {

        try {
            if (StringUtils.isEmpty(apiID) || !apiID.equals(globalConfig.getApiID())) {
                renderFailed("ApiID错误");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("apiID=" + apiID).append("&time=" + time).append(globalConfig.getApiKey());

            String signLocal = EncryptUtils.md5(sb.toString());
            Log.d("getAllChannelMaster: sign str:%s; sign local:%s;sign:%s", sb.toString(), signLocal, sign);
            if (!signLocal.equals(sign)) {
                renderFailed("签名错误");
                return;
            }

            List<UChannelMaster> masters = masterManager.queryAll();

            JSONArray array = new JSONArray();
            for (UChannelMaster m : masters) {
                JSONObject jo = new JSONObject();
                jo.put("masterID", m.getMasterID());
                jo.put("sdkName", m.getSdkName());
                jo.put("masterName", m.getMasterName());
                array.add(jo);
            }

            renderSuc(array);


        } catch (Exception e) {
            renderFailed(e.getMessage());
            e.printStackTrace();
        }


    }

    @Action("metas")
    public void getMasterMetaList() {

        try {

            if (StringUtils.isEmpty(apiID) || !apiID.equals(globalConfig.getApiID())) {
                renderFailed("ApiID错误");
                return;
            }

            if (StringUtils.isEmpty(masterSdkName)) {
                renderFailed("参数错误");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("apiID=" + apiID)
                    .append("&masterSdkName=" + masterSdkName)
                    .append("&time=" + time).append(globalConfig.getApiKey());

            String signLocal = EncryptUtils.md5(sb.toString());
            Log.d("getMasterMetaList: sign str:%s; sign local:%s;sign:%s", sb.toString(), signLocal, sign);
            if (!signLocal.equals(sign)) {
                renderFailed("签名错误");
                return;
            }

            UChannelMaster master = masterManager.getMasterBySDKName(masterSdkName);
            if (master == null) {
                renderFailed("渠道商不存在");
                return;
            }

            List<UChannelParamMeta> metas = metaManager.getMetaListByMaster(master.getMasterID());


            if (metas != null) {
                JSONArray metaArray = new JSONArray();
                for (UChannelParamMeta meta : metas) {
                    JSONObject metaItem = meta.toJSON();
                    metaArray.add(metaItem);
                }
                renderSuc(metaArray);
            } else {
                renderSuc(null);
            }

        } catch (Exception e) {
            renderFailed(e.getMessage());
            e.printStackTrace();
        }

    }

    @Action("saveMeta")
    public void saveMasterMeta() {

        try {

            if (StringUtils.isEmpty(apiID) || !apiID.equals(globalConfig.getApiID())) {
                renderFailed("ApiID错误");
                return;
            }

            if (StringUtils.isEmpty(masterSdkName) || StringUtils.isEmpty(paramKey) || StringUtils.isEmpty(showName)) {
                renderFailed("参数错误");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("apiID=" + apiID);

            if (!StringUtils.isEmpty(defaultVal)) {
                sb.append("&defaultVal=" + defaultVal);
            }

            sb.append("&masterSdkName=" + masterSdkName);

            if (!StringUtils.isEmpty(metaDesc)) {
                sb.append("&metaDesc=" + metaDesc);
            }

            if (!StringUtils.isEmpty(metaID)) {
                sb.append("&metaID=" + metaID);
            }

            sb.append("&paramKey=" + paramKey)
                    .append("&paramStyle=" + paramStyle)
                    .append("&paramType=" + paramType)
                    .append("&pos=" + pos)
                    .append("&showName=" + showName);

            if (!StringUtils.isEmpty(styleExtra)) {
                sb.append("&styleExtra=" + styleExtra);
            }

            sb.append("&time=" + time).append(globalConfig.getApiKey());

            String signLocal = EncryptUtils.md5(sb.toString());
            Log.d("saveMasterMeta: sign str:%s; sign local:%s;sign:%s", sb.toString(), signLocal, sign);
            if (!signLocal.equals(sign)) {
                renderFailed("签名错误");
                return;
            }

            UChannelMaster master = masterManager.getMasterBySDKName(masterSdkName);
            if (master == null) {
                renderFailed("渠道商不存在");
                return;
            }

            if (StringUtils.isEmpty(metaID) || Integer.valueOf(metaID) == 0) {
                //创建新的meta
                UChannelParamMeta exist = metaManager.getMetaByKey(master.getMasterID(), paramKey);
                if (exist != null) {
                    renderFailed("相同key的meta已经存在");
                    return;
                }
                UChannelParamMeta meta = new UChannelParamMeta();
                meta.setMasterID(master.getMasterID());
                meta.setParamKey(paramKey);
                meta.setDefaultVal(defaultVal);
                meta.setMetaDesc(metaDesc);
                meta.setShowName(showName);
                meta.setParamType(Integer.valueOf(paramType));
                meta.setParamStyle(Integer.valueOf(paramStyle));
                meta.setStyleExtra(styleExtra);
                meta.setPos(Integer.valueOf(pos));
                metaManager.saveMeta(meta);

            } else {
                UChannelParamMeta meta = metaManager.getMetaByID(Integer.valueOf(metaID));
                if (meta == null) {
                    renderFailed("meta不存在");
                    return;
                }
                UChannelParamMeta exist = metaManager.getMetaByKey(meta.getMasterID(), paramKey);
                if (exist != null && !exist.getId().equals(meta.getId())) {
                    renderFailed("相同key的meta已经存在");
                    return;
                }
                meta.setParamKey(paramKey);
                meta.setDefaultVal(defaultVal);
                meta.setMetaDesc(metaDesc);
                meta.setShowName(showName);
                meta.setParamType(Integer.valueOf(paramType));
                meta.setParamStyle(Integer.valueOf(paramStyle));
                meta.setStyleExtra(styleExtra);
                meta.setPos(Integer.valueOf(pos));
                metaManager.saveMeta(meta);
            }

            renderSuc(null);

        } catch (Exception e) {
            renderFailed(e.getMessage());
            e.printStackTrace();
        }

    }


    @Action("deleteMeta")
    public void deleteMasterMeta() {

        try {

            if (StringUtils.isEmpty(apiID) || !apiID.equals(globalConfig.getApiID())) {
                renderFailed("ApiID错误");
                return;
            }

            if (StringUtils.isEmpty(metaID) || "0".equals(metaID)) {
                renderFailed("参数错误");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("apiID=" + apiID)
                    .append("&metaID=" + metaID)
                    .append("&time=" + time).append(globalConfig.getApiKey());

            String signLocal = EncryptUtils.md5(sb.toString());
            Log.d("deleteMeta: sign str:%s; sign local:%s;sign:%s", sb.toString(), signLocal, sign);
            if (!signLocal.equals(sign)) {
                renderFailed("签名错误");
                return;
            }

            UChannelParamMeta meta = metaManager.getMetaByID(Integer.valueOf(metaID));
            if (meta == null) {
                renderFailed("meta不存在");
                return;
            }

            metaManager.deleteMeta(meta);
            renderSuc(null);

        } catch (Exception e) {
            renderFailed(e.getMessage());
            e.printStackTrace();
        }

    }


    @Action("channelfullconfig")
    public void getChannelFullConfig() {

        try {
            if (StringUtils.isEmpty(apiID) || !apiID.equals(globalConfig.getApiID())) {
                renderFailed("ApiID错误");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("apiID=" + apiID)
                    .append("&appID=" + appID)
                    .append("&channelID=" + channelID)
                    .append("&time=" + time).append(globalConfig.getApiKey());

            String signLocal = EncryptUtils.md5(sb.toString());
            Log.d("getChannelConfig: sign str:%s; sign local:%s;sign:%s", sb.toString(), signLocal, sign);
            if (!signLocal.equals(sign)) {
                renderFailed("签名错误");
                return;
            }

            UChannel channel = channelManager.getChannel(channelID);
            if (channel == null) {
                Log.e("there is no channel with channel id:" + channelID);
                renderFailed("渠道不存在,请先在U8Server管理后台渠道管理中添加渠道");
                return;
            }

            if (channel.getAppID() != appID) {
                Log.e("appID %s from package tool is not matched with channelID : %s", appID, channelID);
                renderFailed("渠道号所属游戏和指定的游戏不同");
                return;
            }

            JSONObject json = new JSONObject();
            json.put("channelID", channelID);
            List<UChannelParamMeta> metas = channelManager.getParamMetasByChannel(channel);

            if (metas != null) {
                JSONArray metaArray = new JSONArray();
                for (UChannelParamMeta meta : metas) {
                    JSONObject metaItem = meta.toJSON();
                    metaArray.add(metaItem);
                }
                json.put("metas", metaArray);
            }

            List<UChannelProduct> products = productManager.getProducts(channelID);

            JSONArray productArray = new JSONArray();
            if (products != null) {
                for (UChannelProduct m : products) {
                    JSONObject jo = new JSONObject();
                    jo.put("id", m.getId());
                    jo.put("gameProductID", m.getGameProductID());
                    jo.put("channelProductID", m.getChannelProductID());
                    productArray.add(jo);
                }
                json.put("products", productArray);
            }


            renderSuc(json);

        } catch (Exception e) {
            e.printStackTrace();
            renderFailed(e.getMessage());
        }

    }


    @Action("channelconfig")
    public void getChannelConfig() {

        try {

            if (StringUtils.isEmpty(apiID) || !apiID.equals(globalConfig.getApiID())) {
                renderFailed("ApiID错误");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("apiID=" + apiID)
                    .append("&appID=" + appID)
                    .append("&channelID=" + channelID)
                    .append("&time=" + time).append(globalConfig.getApiKey());

            String signLocal = EncryptUtils.md5(sb.toString());
            Log.d("getChannelConfig: sign str:%s; sign local:%s;sign:%s", sb.toString(), signLocal, sign);
            if (!signLocal.equals(sign)) {
                renderFailed("签名错误");
                return;
            }

            UChannel channel = channelManager.getChannel(channelID);
            if (channel == null) {
                Log.e("there is no channel with channel id:" + channelID);
                renderFailed("渠道不存在,请先在U8Server管理后台渠道管理中添加渠道");
                return;
            }

            if (channel.getAppID() != appID) {
                Log.e("appID %s from package tool is not matched with channelID : %s", appID, channelID);
                renderFailed("渠道号所属游戏和指定的游戏不同");
                return;
            }

            List<UChannelParamMeta> metas = channelManager.getParamMetasByChannel(channel);
            if (metas != null) {
                JSONArray metaArray = new JSONArray();
                for (UChannelParamMeta meta : metas) {
                    JSONObject metaItem = meta.toJSON();
                    metaArray.add(metaItem);
                }
                renderSuc(metaArray);
            } else {
                renderSuc(null);
            }


        } catch (Exception e) {
            renderFailed(e.getMessage());
            e.printStackTrace();
        }

    }

    //根据多个渠道号，一次性返回多个渠道对应的参数配置，非法的渠道号自动过滤
    @Action("multichannelconfig")
    public void getMultiChannelConfig() {

        try {

            if (StringUtils.isEmpty(apiID) || !apiID.equals(globalConfig.getApiID())) {
                renderFailed("ApiID错误");
                return;
            }

            if (StringUtils.isEmpty(channelIDs)) {
                renderFailed("没有指定渠道号");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("apiID=" + apiID)
                    .append("&appID=" + appID)
                    .append("&channelIDs=" + channelIDs)
                    .append("&time=" + time).append(globalConfig.getApiKey());

            String signLocal = EncryptUtils.md5(sb.toString());
            Log.d("getMultiChannelConfig: sign str:%s; sign local:%s;sign:%s", sb.toString(), signLocal, sign);
            if (!signLocal.equals(sign)) {
                renderFailed("签名错误");
                return;
            }

            boolean suc = true;
            String msg = "操作成功";

            String[] ids = channelIDs.split(",");
            JSONArray array = new JSONArray();
            for (String id : ids) {
                Integer channelID = Integer.valueOf(id);
                UChannel channel = channelManager.getChannel(channelID);
                if (channel == null) {
                    Log.e("there is no channel with channel id:" + channelID);
                    //renderFailed("渠道不存在,请先在U8Server管理后台渠道管理中添加渠道");
                    suc = false;
                    msg = "部分渠道在U8Server后台不存在,渠道ID:" + channelID;
                    continue;
                }

                if (channel.getAppID() != appID) {
                    Log.e("appID %s from package tool is not matched with channelID : %s", appID, channelID);
                    //renderFailed("渠道号所属游戏和指定的游戏不同");
                    suc = false;
                    msg = "部分渠道ID在U8Server后台中所属游戏和打包工具中不一致,渠道ID:" + channelID;
                    continue;
                }
                JSONObject json = new JSONObject();
                json.put("channelID", channelID);

                List<UChannelParamMeta> metas = channelManager.getParamMetasByChannel(channel);
                if (metas != null) {

                    JSONArray metaArray = new JSONArray();
                    for (UChannelParamMeta meta : metas) {
                        JSONObject metaItem = meta.toJSON();
                        metaArray.add(metaItem);
                    }
                    json.put("metas", metaArray);

                }
                array.add(json);
            }

            if (suc) {
                renderSuc(array);
            } else {
                //有错误
                JSONObject json = new JSONObject();
                json.put("code", 2);
                json.put("msg", msg);
                json.put("data", array);
                renderJson(json.toString());
            }


        } catch (Exception e) {
            renderFailed(e.getMessage());
            e.printStackTrace();
        }

    }

    @Action("saveChannelConfig")
    public void saveChannelParams() {

        try {

            if (StringUtils.isEmpty(apiID) || !apiID.equals(globalConfig.getApiID())) {
                renderFailed("ApiID错误");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("apiID=" + apiID)
                    .append("&appID=" + appID)
                    .append("&channelID=" + channelID)
                    .append("&channelParams=" + channelParams)
                    .append("&products=" + products)
                    .append("&time=" + time).append(globalConfig.getApiKey());

            String signLocal = EncryptUtils.md5(sb.toString());
            Log.d("saveChannelConfig: sign str:%s; sign local:%s;sign:%s", sb.toString(), signLocal, sign);
            if (!signLocal.equals(sign)) {
                renderFailed("签名错误");
                return;
            }

            UChannel channel = channelManager.getChannel(channelID);
            if (channel == null) {
                Log.e("there is no channel with channel id:" + channelID);
                renderFailed("渠道不存在,请先在U8Server管理后台渠道管理中添加渠道");
                return;
            }

            if (channel.getAppID() != appID) {
                Log.e("appID %s from package tool is not matched with channelID : %s", appID, channelID);
                renderFailed("渠道号所属游戏和指定的游戏不同");
                return;
            }

            channel.setChannelParams(channelParams);

            channelManager.saveChannel(channel);

            //保存商品
            if (!StringUtils.isEmpty(products)) {
                productManager.saveMultiProductsFromJson(channelID, products);
            }

            renderSuc(null);


        } catch (Exception e) {
            renderFailed(e.getMessage());
            e.printStackTrace();
        }

    }

    @Action("channelproducts")
    public void getChannelProducts() {

        try {
            if (StringUtils.isEmpty(apiID) || !apiID.equals(globalConfig.getApiID())) {
                renderFailed("ApiID错误");
                return;
            }

            UChannel channel = channelManager.getChannel(channelID);
            if (channel == null) {
                renderFailed("渠道不存在");
                return;
            }

            UGame game = gameManager.getGame(appID);
            if (game == null) {
                renderFailed("游戏不存在");
                return;
            }

            if (appID != channel.getAppID()) {

                renderFailed("渠道号和所属游戏不一致");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("apiID=" + apiID).append("&appID=" + appID).append("&channelID=" + channelID).append("&time=" + time).append(globalConfig.getApiKey());

            String signLocal = EncryptUtils.md5(sb.toString());
            Log.d("getChannelProducts: sign str:%s; sign local:%s;sign:%s", sb.toString(), signLocal, sign);
            if (!signLocal.equals(sign)) {
                renderFailed("签名错误");
                return;
            }

            List<UChannelProduct> products = productManager.getProducts(channelID);

            JSONArray array = new JSONArray();
            if (products != null) {
                for (UChannelProduct m : products) {
                    JSONObject jo = new JSONObject();
                    jo.put("id", m.getId());
                    jo.put("gameProductID", m.getGameProductID());
                    jo.put("channelProductID", m.getChannelProductID());
                    array.add(jo);
                }
            }

            renderSuc(array);


        } catch (Exception e) {
            renderFailed(e.getMessage());
            e.printStackTrace();
        }
    }

    @Action("saveChannelProduct")
    public void saveChannelProducts() {

        try {
            if (StringUtils.isEmpty(apiID) || !apiID.equals(globalConfig.getApiID())) {
                renderFailed("ApiID错误");
                return;
            }

            if (StringUtils.isEmpty(channelProductID) || StringUtils.isEmpty(gameProductID)) {
                renderFailed("参数不能为空");
                return;
            }

            UChannel channel = channelManager.getChannel(channelID);
            if (channel == null) {
                renderFailed("渠道不存在");
                return;
            }

            UGame game = gameManager.getGame(appID);
            if (game == null) {
                renderFailed("游戏不存在");
                return;
            }

            if (appID != channel.getAppID()) {

                renderFailed("渠道号和所属游戏不一致");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("apiID=" + apiID)
                    .append("&appID=" + appID)
                    .append("&channelID=" + channelID)
                    .append("&channelProductID=" + channelProductID)
                    .append("&gameProductID=" + gameProductID);
            if (!StringUtils.isEmpty(productID)) {
                sb.append("&productID=" + productID);
            }
            sb.append("&time=" + time)
                    .append(globalConfig.getApiKey());

            String signLocal = EncryptUtils.md5(sb.toString());
            Log.d("saveChannelProducts: sign str:%s; sign local:%s;sign:%s", sb.toString(), signLocal, sign);
            if (!signLocal.equals(sign)) {
                renderFailed("签名错误");
                return;
            }

            if (StringUtils.isEmpty(productID) || "0".equals(productID)) {
                //新建

                UChannelProduct exist = productManager.getChannelProduct(channelID, gameProductID);
                if (exist != null) {
                    renderFailed("商品已经存在");
                    return;
                }

                UChannelProduct p = new UChannelProduct();
                p.setChannelID(channelID);
                p.setChannelProductID(channelProductID);
                p.setGameProductID(gameProductID);
                productManager.saveChannelProduct(p);
            } else {

                //编辑

                UChannelProduct product = productManager.getProductByID(Integer.valueOf(productID));
                if (product == null) {
                    renderFailed("商品不存在");
                    return;
                }

                UChannelProduct exist = productManager.getChannelProduct(channelID, gameProductID);
                if (exist != null && !exist.getId().equals(product.getId())) {
                    renderFailed("商品已经存在");
                    return;
                }

                product.setGameProductID(gameProductID);
                product.setChannelProductID(channelProductID);
                productManager.saveChannelProduct(product);
            }

            renderSuc(null);


        } catch (Exception e) {
            renderFailed(e.getMessage());
            e.printStackTrace();
        }
    }

    @Action("deleteChannelProduct")
    public void deleteChannelProduct() {
        try {

            if (StringUtils.isEmpty(apiID) || !apiID.equals(globalConfig.getApiID())) {
                renderFailed("ApiID错误");
                return;
            }

            if (StringUtils.isEmpty(productID)) {
                renderFailed("productID不能为空");
                return;
            }

            UChannel channel = channelManager.getChannel(channelID);
            if (channel == null) {
                renderFailed("渠道不存在");
                return;
            }

            UGame game = gameManager.getGame(appID);
            if (game == null) {
                renderFailed("游戏不存在");
                return;
            }

            if (appID != channel.getAppID()) {

                renderFailed("渠道号和所属游戏不一致");
                return;
            }

            StringBuilder sb = new StringBuilder();
            sb.append("apiID=" + apiID)
                    .append("&appID=" + appID)
                    .append("&channelID=" + channelID)
                    .append("&productID=" + productID)
                    .append("&time=" + time)
                    .append(globalConfig.getApiKey());

            String signLocal = EncryptUtils.md5(sb.toString());
            Log.d("saveChannelProducts: sign str:%s; sign local:%s;sign:%s", sb.toString(), signLocal, sign);
            if (!signLocal.equals(sign)) {
                renderFailed("签名错误");
                return;
            }


            UChannelProduct product = productManager.getProductByID(Integer.valueOf(productID));
            if (product == null) {
                renderFailed("商品不存在");
                return;
            }

            productManager.deleteChannelProduct(product);
            renderSuc(null);
            return;

        } catch (Exception e) {
            e.printStackTrace();
            renderFailed(e.getMessage());
        }
    }


    private void renderSuc(Object data) {

        JSONObject json = new JSONObject();
        json.put("code", 1);
        json.put("msg", "success");
        if (data != null)
            json.put("data", data);
        renderJson(json.toString());

    }

    public void renderFailed(String msg) {
        JSONObject json = new JSONObject();
        json.put("code", 0);
        json.put("msg", msg);
        renderJson(json.toString());
    }


    public String getExcludeAppIDs() {
        return excludeAppIDs;
    }

    public void setExcludeAppIDs(String excludeAppIDs) {
        this.excludeAppIDs = excludeAppIDs;
    }

    public String getApiID() {
        return apiID;
    }

    public void setApiID(String apiID) {
        this.apiID = apiID;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
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

    public String getChannelParams() {
        return channelParams;
    }

    public void setChannelParams(String channelParams) {
        this.channelParams = channelParams;
    }

    public String getChannelIDs() {
        return channelIDs;
    }

    public void setChannelIDs(String channelIDs) {
        this.channelIDs = channelIDs;
    }

    public String getProductID() {
        return productID;
    }

    public void setProductID(String productID) {
        this.productID = productID;
    }

    public String getChannelProductID() {
        return channelProductID;
    }

    public void setChannelProductID(String channelProductID) {
        this.channelProductID = channelProductID;
    }

    public String getGameProductID() {
        return gameProductID;
    }

    public void setGameProductID(String gameProductID) {
        this.gameProductID = gameProductID;
    }

    public String getMasterSdkName() {
        return masterSdkName;
    }

    public void setMasterSdkName(String masterSdkName) {
        this.masterSdkName = masterSdkName;
    }

    public String getParamKey() {
        return paramKey;
    }

    public void setParamKey(String paramKey) {
        this.paramKey = paramKey;
    }

    public String getShowName() {
        return showName;
    }

    public void setShowName(String showName) {
        this.showName = showName;
    }

    public String getMetaDesc() {
        return metaDesc;
    }

    public void setMetaDesc(String metaDesc) {
        this.metaDesc = metaDesc;
    }

    public String getDefaultVal() {
        return defaultVal;
    }

    public void setDefaultVal(String defaultVal) {
        this.defaultVal = defaultVal;
    }

    public String getPos() {
        return pos;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }

    public String getParamType() {
        return paramType;
    }

    public void setParamType(String paramType) {
        this.paramType = paramType;
    }

    public String getMetaID() {
        return metaID;
    }

    public void setMetaID(String metaID) {
        this.metaID = metaID;
    }

    public String getProducts() {
        return products;
    }

    public void setProducts(String products) {
        this.products = products;
    }

    public String getParamStyle() {
        return paramStyle;
    }

    public void setParamStyle(String paramStyle) {
        this.paramStyle = paramStyle;
    }

    public String getStyleExtra() {
        return styleExtra;
    }

    public void setStyleExtra(String styleExtra) {
        this.styleExtra = styleExtra;
    }
}
