package com.u8.server.dao.hibernate;

import com.u8.server.common.Page;
import com.u8.server.common.PageParameter;
import com.u8.server.common.SQLParams;
import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UChannel;
import org.springframework.stereotype.Repository;

/**
 * 渠道数据访问类
 */
@Repository("channelDao")
public class UChannelDao extends UHibernateTemplate<UChannel, Integer>{
    //根据渠道ID获取渠道数据
    public UChannel getByChannelID(int channelID){
        String hql = "from UChannel where channelID = ?";

        return (UChannel)this.findUnique(hql, new Object[]{channelID});
    }


}
