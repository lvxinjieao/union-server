package com.u8.server.dao.hibernate;

import com.u8.server.common.Page;
import com.u8.server.common.PageParameter;
import com.u8.server.common.SQLParams;
import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UChannelMaster;
import com.u8.server.utils.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 渠道商数据访问类
 */
@Repository("channelMasterDao")
public class UChannelMasterDao extends UHibernateTemplate<UChannelMaster, Integer> {

}
