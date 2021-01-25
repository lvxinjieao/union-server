package com.u8.server.data;

import net.sf.json.JSONObject;

import javax.persistence.*;
import java.io.Serializable;

/**
 * 渠道后台商品和游戏商品ID映射关系
 * Created by ant on 2018/5/15.
 */
@Entity
@Table(name = "uchannelproduct")
public class UChannelProduct implements Serializable {

    private static final long serialVersionUID = -5461838578919693325L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;                 //唯一ID

    private Integer channelID;          //渠道ID

    private String gameProductID;       //游戏商品ID

    private String channelProductID;   //该游戏商品对应在渠道后台商品对应的ID

    public JSONObject toJSON(){

        JSONObject json = new JSONObject();
        json.put("id", id);
        json.put("channelID", channelID);
        json.put("gameProductID", gameProductID);
        json.put("channelProductID", channelProductID);

        return json;

    }

    public String getGameProductID() {
        return gameProductID;
    }

    public void setGameProductID(String gameProductID) {
        this.gameProductID = gameProductID;
    }

    public String getChannelProductID() {
        return channelProductID;
    }

    public void setChannelProductID(String channelProductID) {
        this.channelProductID = channelProductID;
    }


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getChannelID() {
        return channelID;
    }

    public void setChannelID(Integer channelID) {
        this.channelID = channelID;
    }
}
