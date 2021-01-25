package com.u8.server.dao.hibernate;

import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.USubChannel;
import org.springframework.stereotype.Repository;

/**
 * 子渠道数据访问
 * Created by ant on 2017/2/7.
 */
@Repository("subChannelDao")
public class USubChannelDao extends UHibernateTemplate<USubChannel, Integer> {



}
