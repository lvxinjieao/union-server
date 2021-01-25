package com.u8.server.dao.hibernate;

import com.u8.server.common.Page;
import com.u8.server.common.PageParameter;
import com.u8.server.common.SQLParams;
import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UChannel;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 渠道数据访问类
 */
@Repository("channelDao")
public class UChannelDao extends UHibernateTemplate<UChannel, Integer>{


    //获取指定游戏的渠道数量(后台管理调用)
    public long getChannelCountByGame(int appID){

        String hql = "select count(c) from UChannel c where appID = ?";

        return this.findLong(hql, new Object[]{appID});

    }

    //获取指定渠道商下的渠道数量(后台管理调用)
    public long getChannelCountByMaster(int masterID){

        String hql = "select count(c) from UChannel c where masterID = ?";

        return this.findLong(hql, new Object[]{masterID});

    }

    //根据渠道ID获取渠道数据
    public UChannel getByChannelID(int channelID){
        String hql = "from UChannel where channelID = ?";

        return (UChannel)this.findUnique(hql, new Object[]{channelID});
    }

    //根据游戏ID获取所有渠道数据
    public List<UChannel> getChannelsByAppID(int appID){

        SQLParams params = new SQLParams();
        if(appID > 0){
            params.EQ("appID", appID);
        }
        return this.find("from UChannel " + params.getWhereSQL(), params.getWhereValues(), null);
    }

    //分页查找，直接从数据库查找
    public Page<UChannel> queryPage(int currPage, int num, Integer channelID, Integer appID, Object[] masterIDs, Object[] permissionedGameIDs) {

        SQLParams params = new SQLParams();
        if(masterIDs != null){
            params.IN("masterID", masterIDs);

        }

        if(channelID != null && channelID > 0){
            params.EQ("channelID", channelID);
        }

        if(appID != null && appID != 0){
            params.EQ("appID", appID);
        }

        if(permissionedGameIDs != null){
            if(permissionedGameIDs.length == 0){
                permissionedGameIDs = new Object[]{0};
            }
            params.IN("appID", permissionedGameIDs);
        }



        PageParameter page = new PageParameter(currPage, num, true);
        String hql = "from UChannel" + params.getWhereSQL();

        return this.find(page, hql, params.getWhereValues(), null);

    }

}
