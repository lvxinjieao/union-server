package com.u8.server.service;

import com.u8.server.dao.hibernate.UUserLogDao;
import com.u8.server.data.UUserLog;
import com.u8.server.log.BLog;
import org.hibernate.Session;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户日志
 * Created by ant on 2016/8/15.
 */
@Service("userLogManager")
public class UUserLogManager {

    @Autowired
    private UUserLogDao userLogDao;


    public void saveUserLog(UUserLog log){
        userLogDao.save(log);

        switch (log.getOpType()){
            case UUserLog.OPTYPE_CREATE_ROLE:
                BLog.getInstance().printJson(BLog.T_USERLOG, BLog.OPTYPE.USERLOG.OP_CREATE_ROLE, log);
                break;
            case UUserLog.OPTYPE_ENTER_GAME:
                BLog.getInstance().printJson(BLog.T_USERLOG, BLog.OPTYPE.USERLOG.OP_ENTER_GAME, log);
                break;
            case UUserLog.OPTYPE_LEVEL_UP:
                BLog.getInstance().printJson(BLog.T_USERLOG, BLog.OPTYPE.USERLOG.OP_LEVEL_UP, log);
                break;
            case UUserLog.OPTYPE_EXIT_GAME:
                BLog.getInstance().printJson(BLog.T_USERLOG, BLog.OPTYPE.USERLOG.OP_EXIT_GAME, log);
                break;
        }


    }

}
