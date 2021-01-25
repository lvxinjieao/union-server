package com.u8.server.dao.hibernate;

import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UUserLog;
import org.springframework.stereotype.Repository;

/**
 * 用户日志采用按照每天一张表进行自动分表
 * Created by ant on 2016/8/15.
 */
@Repository("userLogDao")
public class UUserLogDao extends UHibernateTemplate<UUserLog, Long> {

}
