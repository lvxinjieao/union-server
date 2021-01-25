package com.u8.server.dao.hibernate;

import com.u8.server.common.Page;
import com.u8.server.common.PageParameter;
import com.u8.server.common.SQLParams;
import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UChannelParamMeta;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 *渠道参数meta数据访问类
 * Created by ant on 2018/2/22.
 */
@Repository("channelParamMetaDao")
public class UChannelParamMetaDao extends UHibernateTemplate<UChannelParamMeta, Integer> {


    public Page<UChannelParamMeta> queryPage(int currPage, int num, Integer masterID) {

        SQLParams params = new SQLParams();
        if(masterID != null && masterID > 0){
            params.EQ("masterID", masterID);
        }


        PageParameter page = new PageParameter(currPage, num, true);
        String hql = "from UChannelParamMeta" + params.getWhereSQL();

        return this.find(page, hql, params.getWhereValues(), null);

    }

    /**
     * 根据渠道商ID获取对应的参数配置meta
     * @param masterID
     * @return
     */
    public List<UChannelParamMeta> getMetaListByMaster(Integer masterID){

        String hql = "from UChannelParamMeta where masterID = ?";

        return this.find(hql, new Object[]{masterID}, null);

    }

    /**
     * 根据key获取参数配置meta
     * @param masterID
     * @param key
     * @return
     */
    public UChannelParamMeta getMetaByKey(Integer masterID, String key){

        String hql = "from UChannelParamMeta where masterID = ? and paramKey = ?";

        return (UChannelParamMeta)this.findUnique(hql, new Object[]{masterID, key});
    }

}
