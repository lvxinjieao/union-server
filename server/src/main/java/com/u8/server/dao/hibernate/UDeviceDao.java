package com.u8.server.dao.hibernate;

import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UDevice;
import org.springframework.stereotype.Repository;

/**
 * Created by ant on 2016/8/12.
 */
@Repository("deviceDao")
public class UDeviceDao extends UHibernateTemplate<UDevice, Integer>{

}
