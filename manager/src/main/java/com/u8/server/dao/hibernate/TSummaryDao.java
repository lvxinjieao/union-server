package com.u8.server.dao.hibernate;

import com.u8.server.common.*;
import com.u8.server.data.analytics.SummaryInfo;
import com.u8.server.data.analytics.TSummary;
import com.u8.server.log.Log;
import com.u8.server.utils.TimeUtils;
import org.hibernate.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by ant on 2016/8/26.
 */
@Repository("summaryDao")
public class TSummaryDao extends UHibernateTemplate<TSummary, Integer> {


    /**
     * 获取指定游戏指定时间段内的概要信息
     * @param appID
     * @param from
     * @param to
     * @return
     */
    public SummaryInfo querySummaryInfo(int appID, Date from, Date to){

        String sql = "select sum(deviceNum), sum(userNum), sum(payUserNum), sum(money) from tsummary where appID=?";
        //sql += " and currTime >= '"+ TimeUtils.format_default(from)+"' and currTIme <= '"+TimeUtils.format_default(to) + "'";
        Object[] params = new Object[]{appID};
        if(from != null && to != null){
            sql += " and currTime >= ? and currTime <= ?";
            params = new Object[]{appID, TimeUtils.format_default(TimeUtils.dateBegin(from)), TimeUtils.format_default(TimeUtils.dateEnd(to))};
        }
        Query q = createSQLQuery(sql, params);
        Object[] result = (Object[])q.uniqueResult();

        SummaryInfo summary = new SummaryInfo();
        summary.setDeviceNum(result[0] == null ? 0 : Long.valueOf(result[0].toString()));
        summary.setUserNum(result[1] == null ? 0 : Long.valueOf(result[1].toString()));
        summary.setPayUserNum(result[2] == null ? 0 : Long.valueOf(result[2].toString()));
        summary.setPayMoney(result[3] == null ? 0 : Long.valueOf(result[3].toString()));
        summary.setSummaryData(querySummaryData(appID, from == null ? null : TimeUtils.dateSub(from, 1), to));        //少一天BUG

        return summary;

    }

    /**
     * 获取指定游戏指定时间段内的所有统计信息
     * @param appID
     * @param from
     * @param to
     * @return
     */
    public List<TSummary> querySummaryData(int appID, Date from, Date to){

        String hql = "from TSummary where appID = ?";
        Object[] params = new Object[]{appID};
        if (from != null && to != null){
            hql += " and currTime >= ? and currTime <= ?";
            params = new Object[]{appID, TimeUtils.dateBegin(from), TimeUtils.dateEnd(to)};

        }

        return super.find(hql, params, null);
    }

    /**
     * 分页查询
     * @param currPage
     * @param num
     * @param appID
     * @param beginCreateTime
     * @param endCreateTime
     * @return
     */
    public Page<TSummary> querySummaryPage(int currPage, int num, Integer appID, Date beginCreateTime, Date endCreateTime){

        SQLParams params = new SQLParams();
        if(appID != null && appID > 0){
            params.EQ("appID", appID);
        }

        if(beginCreateTime != null){
            params.GE("currTime", beginCreateTime);
        }
        if(endCreateTime != null){
            params.LE("currTime", endCreateTime);
        }


        String hql = "from TSummary " + params.getWhereSQL();

        Log.d("tsummary search hql is ");
        Log.d(hql);

        PageParameter page = new PageParameter(currPage, num, true);
        OrderParameters order = new OrderParameters();
        order.add("currTime", OrderParameter.OrderType.DESC);

        return super.find(page, hql, params.getWhereValues(), order);

    }

}
