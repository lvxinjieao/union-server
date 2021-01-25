package com.u8.server.dao.hibernate;

import com.u8.server.common.Page;
import com.u8.server.common.PageParameter;
import com.u8.server.common.SQLParams;
import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.USubChannel;
import org.springframework.stereotype.Repository;

/**
 * 子渠道数据访问
 * Created by ant on 2017/2/7.
 */
@Repository("subChannelDao")
public class USubChannelDao extends UHibernateTemplate<USubChannel, Integer> {


    //分页查找，直接从数据库查找
    public Page<USubChannel> queryPage(int currPage, int num, Integer channelID, Integer appID, Integer subChannelID, Object[] permissionedGameIDs) {

        SQLParams params = new SQLParams();

        if(channelID != null && channelID > 0){
            params.EQ("channelID", channelID);
        }

        if(appID != null && appID != 0){
            params.EQ("appID", appID);
        }

        if(subChannelID != null && subChannelID > 0){
            params.EQ("subChannelID", subChannelID);
        }

        if(permissionedGameIDs != null){
            if(permissionedGameIDs.length == 0){
                permissionedGameIDs = new Object[]{0};
            }
            params.IN("appID", permissionedGameIDs);
        }



        PageParameter page = new PageParameter(currPage, num, true);
        String hql = "from USubChannel" + params.getWhereSQL();

        return this.find(page, hql, params.getWhereValues(), null);

    }

}
