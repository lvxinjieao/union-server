package com.u8.server.service;

import com.u8.server.dao.hibernate.UPlatformUserDao;
import com.u8.server.data.UOrder;
import com.u8.server.data.UPlatformUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by ant on 2017/7/7.
 */
@Service("platformUserManager")
public class UPlatformUserManager {

    @Autowired
    private UPlatformUserDao platformUserDao;

    public void saveUser(UPlatformUser user){
        platformUserDao.save(user);
    }

    public UPlatformUser getUserByID(int id){
        return platformUserDao.get(id);
    }

    public UPlatformUser getUserByName(String name){
        String hql = "from UPlatformUser where name = ?";
        return (UPlatformUser) platformUserDao.findUnique(hql, new Object[]{name});
    }

}
