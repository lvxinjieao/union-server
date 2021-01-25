package com.u8.server.service;

import com.u8.server.common.Page;
import com.u8.server.common.PageParameter;
import com.u8.server.common.SQLParams;
import com.u8.server.dao.hibernate.UCPInfoDao;
import com.u8.server.data.UCPInfo;
import com.u8.server.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by ant on 2017/8/31.
 */
@Service("cpinfoManager")
public class UCPInfoManager {

    @Autowired
    private UCPInfoDao cpinfoDao;

    public Page<UCPInfo> getCPInfoPage(int currPage, int rows, Integer cpID, String cpName){

        PageParameter page = new PageParameter(currPage, rows, true);


        SQLParams params = new SQLParams();
        if(cpID != null && cpID > 0){
            params.EQ("id", cpID);
        }

        if(!StringUtils.isEmpty(cpName)){
            params.Like("name", cpName);
        }

        String hql = "from UCPInfo " + params.getWhereSQL();

        return cpinfoDao.find(page, hql, params.getWhereValues(), null);
    }

    public List<UCPInfo> getAllCPList(){

        return cpinfoDao.findAll();
    }

    public UCPInfo getCPByID(int id){

        return cpinfoDao.get(id);
    }

    public void deleteCP(UCPInfo cp){

        cpinfoDao.delete(cp);
    }

    public void addCP(UCPInfo cp){
        cp.setCreateTime(new Date());
        cpinfoDao.save(cp);
    }

    public void updateCP(UCPInfo cp){
        cpinfoDao.save(cp);
    }

}
