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


    public UChannelMaster getChannelMasterBySDKName(String sdkName){
        String hql = "from UChannelMaster where sdkName = ?";
        List<UChannelMaster> masters = this.find(hql, new Object[]{sdkName}, null);
        if(masters != null && masters.size() > 0){
            return masters.get(0);
        }
        return null;
    }

    //获取指定名称的渠道商数据
    public List<UChannelMaster> getChannelMastersByName(String name){
        SQLParams params = new SQLParams();
        if(!StringUtils.isEmpty(name)){
            params.Like("masterName", "%"+name+"%");
        }
        String hql = "from UChannelMaster " + params.getWhereSQL();

        return this.find(hql, params.getWhereValues(), null);
    }


    public Page<UChannelMaster> queryPage(int currPage, int num, Integer masterID, String sdkName, String masterName, String nameSuffix,
                                          String verifyClass) {

        SQLParams params = new SQLParams();
        if(masterID != null && masterID > 0){
            params.EQ("masterID", masterID);
        }

        if(!StringUtils.isEmpty(sdkName)){
            params.Like("sdkName", "%"+sdkName+"%");
        }

        if(!StringUtils.isEmpty(masterName)){
            params.Like("masterName", "%"+masterName+"%");
        }

        if(!StringUtils.isEmpty(nameSuffix)){
            params.EQ("nameSuffix", nameSuffix);
        }

        if(!StringUtils.isEmpty(verifyClass)){
            params.EQ("verifyClass", verifyClass);
        }

        PageParameter page = new PageParameter(currPage, num, true);
        String hql = "from UChannelMaster" + params.getWhereSQL();

        return this.find(page, hql, params.getWhereValues(), null);

    }

}
