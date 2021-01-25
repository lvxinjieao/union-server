package com.u8.server.service;

import com.u8.server.dao.hibernate.UAdminDao;
import com.u8.server.dao.hibernate.UGameDao;
import com.u8.server.data.UAdmin;
import com.u8.server.data.UAdminRole;
import com.u8.server.data.UGame;
import com.u8.server.utils.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ant on 2015/8/29.
 */

@Service("adminManager")
public class UAdminManager {

    @Autowired
    private UAdminDao adminDao;

    @Autowired
    private UAdminRoleManager adminRoleManager;

    @Autowired
    private UGameDao gameDao;

    public UAdmin getAdmin(int id){
        return adminDao.get(id);
    }

    public void saveAdmin(UAdmin admin){
        adminDao.save(admin);
    }

    public void deleteAdmin(UAdmin admin){
        adminDao.delete(admin);
    }

    public UAdmin getAdminByUsername(String username){

        return adminDao.getAdminByUsername(username);
    }

    /**
     * 获取拥有指定游戏管理权限的所有管理员
     * @param appID
     * @return
     */
    public List<UAdmin> getAllAdminsByGameID(int appID){
        List<UAdmin> admins = getAllAdmins();
        List<UAdmin> result = new ArrayList<UAdmin>();
        for(UAdmin admin : admins){
            if(admin.getAdminGames() != null){
                String[] gameIDs = admin.getAdminGames().split(",");
                for (String id : gameIDs){
                    if(StringUtils.isEmpty(id)){
                        continue;
                    }
                    int currID = Integer.valueOf(id);
                    if(currID == appID){
                        result.add(admin);
                        break;
                    }
                }
            }

        }

        return result;
    }

    //往指定的管理员身上分配一个游戏权限
    public void addAdminGamePermission(UAdmin admin, int appID){

        if(appID <= 0){
            return;
        }

        if(admin.getAdminGames() == null){
            admin.setAdminGames(appID+"");
        }

        String[] ids = admin.getAdminGames().split(",");
        for(String id : ids){
            if(StringUtils.isEmpty(id)){
                continue;
            }
            int gameID = Integer.valueOf(id);
            if(gameID == appID){
                return;
            }
        }

        admin.setAdminGames(admin.getAdminGames()+","+appID);
        adminDao.save(admin);
    }

    //从指定的管理员身上删除一个游戏权限
    public void removeAdminGamePermission(UAdmin admin, int appID){

        if(appID <= 0){
            return;
        }

        if(admin.getAdminGames() == null || StringUtils.isEmpty(admin.getAdminGames())){
            return;
        }

        String[] ids = admin.getAdminGames().split(",");
        String result = "";
        for(String id : ids){
            if(StringUtils.isEmpty(id)){
                continue;
            }
            int gameID = Integer.valueOf(id);
            if(gameID == appID){
                continue;
            }
            result += id + ",";
        }

        if(result.endsWith(",")){
            result = result.substring(0, result.length()-1);
        }

        admin.setAdminGames(result);
        adminDao.save(admin);
    }

    public Object[] getPermissonedGameIDs( UAdmin admin){

        UAdminRole role = adminRoleManager.getAdminRoleByID(admin.getAdminRoleID());

        if(role.getTopRole() != null && role.getTopRole().equals(1)){
            List<UGame> games = gameDao.findAll();
            List<Integer> ids = new ArrayList<Integer>();
            for(UGame game : games){
                ids.add(game.getAppID());
            }
            return ids.toArray();
        }

        if(admin.getAdminGames() != null){
            String games = admin.getAdminGames();
            if(games.startsWith(",")){
                games = games.substring(1);
            }
            String[] ids = games.split(",");
            Object[] result = new Object[ids.length];
            for(int i=0; i<ids.length;i++){
                if(StringUtils.isEmpty(ids[i])){
                    result[i] = 0;
                }else{
                    result[i] = Integer.valueOf(ids[i]);
                }

            }
            return result;
        }
        return new Object[]{};
    }


    public List<UAdmin> getAllAdmins(){

        return adminDao.findAll();
    }
}
