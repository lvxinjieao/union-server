package com.u8.server.dao.hibernate;

import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UCPInfo;
import org.springframework.stereotype.Repository;

/**
 * Created by ant on 2017/8/31.
 */
@Repository("cpinfoDao")
public class UCPInfoDao extends UHibernateTemplate<UCPInfo, Integer> {



}
