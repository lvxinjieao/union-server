package com.u8.server.dao.hibernate;

import com.u8.server.common.Page;
import com.u8.server.common.PageParameter;
import com.u8.server.common.SQLParams;
import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UChannelProduct;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by ant on 2018/5/15.
 */
@Repository("channelProductDao")
public class UChannelProductDao extends UHibernateTemplate<UChannelProduct, Integer> {

    public UChannelProduct getChannelProduct(int channelID, String gameProductID){

        String hql = "from UChannelProduct where channelID = ? and gameProductID = ?";

        return (UChannelProduct) findUnique(hql, new Object[]{channelID, gameProductID});

    }

    //分页查找，直接从数据库查找
    public Page<UChannelProduct> queryPage(int currPage, int num, Integer channelID) {

        SQLParams params = new SQLParams();

        if(channelID != null && channelID > 0){
            params.EQ("channelID", channelID);
        }

        PageParameter page = new PageParameter(currPage, num, true);
        String hql = "from UChannelProduct" + params.getWhereSQL();

        return this.find(page, hql, params.getWhereValues(), null);

    }

    //根据渠道号查询商品映射关系
    public List<UChannelProduct> getProductsByChannelID(int channelID){

        String hql = "from UChannelProduct where channelID = ?";

        return find(hql, new Object[]{channelID}, null);

    }

}
