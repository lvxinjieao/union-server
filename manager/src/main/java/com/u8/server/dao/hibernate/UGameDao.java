package com.u8.server.dao.hibernate;

import com.u8.server.common.Page;
import com.u8.server.common.PageParameter;
import com.u8.server.common.SQLParams;
import com.u8.server.common.UHibernateTemplate;
import com.u8.server.data.UGame;
import com.u8.server.utils.StringUtils;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * 游戏对象数据访问类
 */
@Repository("gameDao")
public class UGameDao extends UHibernateTemplate<UGame, Integer>{


    /**
     * 根据多个appID获取多个ugame对象
     * @param appIDs
     * @return
     */
    public List<UGame> getGamesByAppIds(Object[] appIDs){
        SQLParams params = new SQLParams();
        if(appIDs != null && appIDs.length > 0){
            params.IN("appID", appIDs);
        }
        String hql = "from UGame" + params.getWhereSQL() +" order by appID desc";;
        return this.find(hql, params.getWhereValues(), null);
    }
    /**
     * 根据多个appID获取多个ugame对象
     * @param appIDs
     * @return
     */
    public List<UGame> getGamesExclude(Object[] appIDs){
        SQLParams params = new SQLParams();
        if(appIDs != null && appIDs.length > 0){
            params.NOTIN("appID", appIDs);
        }
        String hql = "from UGame" + params.getWhereSQL();

        return this.find(hql, params.getWhereValues(), null);
    }


    /**
     * 根据游戏名称获取游戏对象
     * @param name
     * @return
     */
    public List<UGame> getGamesByName(String name){
        SQLParams params = new SQLParams();
        if(!StringUtils.isEmpty(name)){
            params.Like("name", name);
        }
        String hql = "from UGame " + params.getWhereSQL();

        return this.find(hql, params.getWhereValues(), null);
    }

    //分页查找， 直接从数据库中查找
    public Page<UGame> queryPage(int currPage, int num, Integer appID, String appKey, String appSecret, String name, Object[] permissionedGameIDs){

        SQLParams params = new SQLParams();
        if(appID != null && appID > 0){
            params.EQ("appID", appID);
        }

        if(!StringUtils.isEmpty(appKey)){
            params.EQ("appkey", appKey);
        }

        if(!StringUtils.isEmpty(appSecret)){
            params.EQ("appSecret", appSecret);
        }

        if(!StringUtils.isEmpty(name)){
            params.EQ("name", name);
        }

        if(permissionedGameIDs != null){
            if(permissionedGameIDs.length == 0){
                permissionedGameIDs = new Object[]{1};
                permissionedGameIDs[0] = 0;
            }
            params.IN("appID", permissionedGameIDs);
        }

        PageParameter page = new PageParameter(currPage, num, true);
        String hql = "from UGame" + params.getWhereSQL();

        return this.find(page, hql, params.getWhereValues(), null);
    }

}
