package com.u8.server.data;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 * U8平台测试账户数据
 * Created by ant on 2017/7/7.
 */
@Entity
@Table(name = "uplatformuser")
public class UPlatformUser implements Serializable {

    private static final long serialVersionUID = 6843308368132746365L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;     //唯一ID
    private String name;    //用户名
    private String password;//密码
    private Date createTime;

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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
