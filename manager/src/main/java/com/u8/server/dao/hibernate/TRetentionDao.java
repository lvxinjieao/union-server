package com.u8.server.dao.hibernate;

import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.analytics.TRetention;
import com.u8.server.utils.TimeUtils;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by ant on 2016/8/29.
 */
@Repository("retentionDao")
public class TRetentionDao extends UHibernateTemplate<TRetention, Integer> {

    /**
     * 获取指定游戏指定时间段内的所有统计信息
     * @param appID
     * @param from
     * @param to
     * @return
     */
    public List<TRetention> queryRetentionData(int appID, Date from, Date to){

        String hql = "from TRetention where appID = ?";
        Object[] params = new Object[]{appID};
        if (from != null && to != null){
            hql += " and statTime >= ? and statTime <= ?";
            params = new Object[]{appID, TimeUtils.dateBegin(from), TimeUtils.dateEnd(to)};

        }

        return super.find(hql, params, null);
    }

}
