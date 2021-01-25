package com.u8.server.data;

import net.sf.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 渠道参数配置项描述
 * Created by ant on 2018/2/22.
 */
@Entity
@Table(name = "uchannelparammeta")
public class UChannelParamMeta implements Serializable {

    private static final long serialVersionUID = -6696354947329643689L;

    public static final int POS_NONE = 0;
    public static final int POS_CONFIG = 1;
    public static final int POS_METADATA = 2;
    public static final int POS_BOTH = 3;

    public static final int TYPE_NORMAL = 0;
    public static final int TYPE_CLIENT = 1;
    public static final int TYPE_SERVER = 2;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;           //唯一ID

    private Integer masterID;       //渠道商ID

    private String paramKey;        //参数唯一Key

    private String showName;        //参数显示名称

    private String metaDesc;            //参数配置说明

    private String defaultVal;      //默认值

    private Integer pos;            //参数位置， 0：都不写入；1：写入assets下面配置文件；2：写入meta-data；3：都写入

    private Integer paramType;     //参数类型， 0：通用; 1:客户端专用;2:服务器端专用

    private Integer paramStyle = 0;     //参数展示类别， 0：文本;1:列表(styleExtra中，用|分割多个固定值);2:文件(styleExtra中保存文件在打包工具中的相对路径)

    private String styleExtra;          //不同展示类别，如果需要设置附加信息，可以在这里设置

    public JSONObject toJSON(){

        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("masterID", masterID);
        json.put("paramKey", paramKey);
        json.put("showName", showName);
        json.put("metaDesc", metaDesc);
        json.put("defaultVal", defaultVal);
        json.put("styleExtra", styleExtra);

        int pos = 0;
        if(this.pos != null){
            pos = this.pos;
        }

        json.put("pos", pos);
//        switch(pos){
//            case POS_NONE:
//                json.put("posName", "不存储");
//                break;
//            case POS_CONFIG:
//                json.put("posName", "配置文件");
//                break;
//            case POS_METADATA:
//                json.put("posName", "meta-data");
//                break;
//            case POS_BOTH:
//                json.put("posName", "都存储");
//                break;
//
//        }

        int type = 0;
        if(this.paramType != null){
            type = this.paramType;
        }
        json.put("paramType", type);
        switch(type){
            case TYPE_NORMAL:
                json.put("paramTypeName", "前后端通用");
                break;
            case TYPE_CLIENT:
                json.put("paramTypeName", "客户端专用");
                break;
            case TYPE_SERVER:
                json.put("paramTypeName", "服务端专用");
                break;
        }

        json.put("paramStyle", paramStyle);

        return json;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getMasterID() {
        return masterID;
    }

    public void setMasterID(Integer masterID) {
        this.masterID = masterID;
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

    public Integer getPos() {
        return pos;
    }

    public void setPos(Integer pos) {
        this.pos = pos;
    }

    public Integer getParamType() {
        return paramType;
    }

    public void setParamType(Integer paramType) {
        this.paramType = paramType;
    }

    public Integer getParamStyle() {
        return paramStyle;
    }

    public void setParamStyle(Integer paramStyle) {
        this.paramStyle = paramStyle;
    }

    public String getStyleExtra() {
        return styleExtra;
    }

    public void setStyleExtra(String styleExtra) {
        this.styleExtra = styleExtra;
    }
}
