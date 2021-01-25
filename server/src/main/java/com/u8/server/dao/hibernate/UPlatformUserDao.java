package com.u8.server.dao.hibernate;

import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UOrder;
import com.u8.server.data.UPlatformUser;
import org.springframework.stereotype.Repository;

/**
 * U8平台测试账户操作
 * Created by ant on 2017/7/7.
 */
@Repository("platformUserDao")
public class UPlatformUserDao extends UHibernateTemplate<UPlatformUser, Integer> {
}
