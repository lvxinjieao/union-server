package com.u8.server.data;

import net.sf.json.JSONObject;

import javax.persistence.*;

/**
 * Created by ant on 2015/8/29.
 */
@Entity
@Table(name = "uadmin")
public class UAdmin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;            //用户名
    private String password;            //密码
    private Integer adminRoleID;        //所属管理角色
    private String adminRoleName;       //角色名称
    private String adminGames;          //可以管理的游戏

    public JSONObject toJSON(){

        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("username", username);
        json.put("password", password);
        json.put("adminRoleID", adminRoleID);
        json.put("adminRoleName", adminRoleName);
        json.put("adminGames", adminGames);
        return json;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Integer getAdminRoleID() {
        return adminRoleID;
    }

    public void setAdminRoleID(Integer adminRoleID) {
        this.adminRoleID = adminRoleID;
    }

    public String getAdminRoleName() {
        return adminRoleName;
    }

    public void setAdminRoleName(String adminRoleName) {
        this.adminRoleName = adminRoleName;
    }

    public String getAdminGames() {
        return adminGames;
    }

    public void setAdminGames(String adminGames) {
        this.adminGames = adminGames;
    }
}
