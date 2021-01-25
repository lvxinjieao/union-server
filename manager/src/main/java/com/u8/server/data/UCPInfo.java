package com.u8.server.data;

import com.u8.server.utils.TimeUtils;
import net.sf.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * CP信息(手游产品提供商)--对应合作公司信息
 * Created by ant on 2017/8/31.
 */
@Entity
@Table(name = "ucpinfo")
public class UCPInfo implements Serializable{

    private static final long serialVersionUID = 2835651400865043259L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;             //主键, 唯一ID
    private String name;            //CP名称
    private String owner;           //法人姓名
    private String addr;            //地址
    private String phoneNum;        //联系电话
    private String contactName;    //商务联系人
    private String contactQQ;      //商务联系人QQ
    private String contactWX;      //商务联系人微信
    private String contactPN;      //商务联系人电话
    private Date createTime;        //创建时间


    public JSONObject toJSON(){
        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("name", name);
        json.put("owner", owner == null? "": owner);
        json.put("addr", addr == null? "":addr);
        json.put("phoneNum", phoneNum == null ? "": phoneNum);
        json.put("contactName", contactName == null ? "":contactName);
        json.put("contactQQ", contactQQ == null ? "" : contactQQ);
        json.put("contactWX", contactWX == null ? "" : contactWX);
        json.put("contactPN", contactPN == null ? "" : contactPN);
        json.put("createTime", TimeUtils.format_default(new Date()));

        return json;
    }

    public void copyFrom(UCPInfo cp){

        this.name = cp.name;
        this.owner = cp.owner;
        this.addr = cp.addr;
        this.phoneNum = cp.phoneNum;
        this.contactName = cp.contactName;
        this.contactQQ =   cp.contactQQ;
        this.contactWX =   cp.contactWX;
        this.contactPN =   cp.contactPN;


    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getAddr() {
        return addr;
    }

    public void setAddr(String addr) {
        this.addr = addr;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getContactQQ() {
        return contactQQ;
    }

    public void setContactQQ(String contactQQ) {
        this.contactQQ = contactQQ;
    }

    public String getContactWX() {
        return contactWX;
    }

    public void setContactWX(String contactWX) {
        this.contactWX = contactWX;
    }

    public String getContactPN() {
        return contactPN;
    }

    public void setContactPN(String contactPN) {
        this.contactPN = contactPN;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
