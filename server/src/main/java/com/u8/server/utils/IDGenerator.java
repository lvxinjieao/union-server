package com.u8.server.utils;

import com.u8.server.cache.UApplicationContext;
import com.u8.server.config.GlobalConfig;
import com.u8.server.log.Log;
import org.tuckey.web.filters.urlrewrite.Run;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/***
 * 这个是一个简单的唯一ID生成器
 */
public class IDGenerator {

    private static IDGenerator instance;

    private int serverID = 0;
    private long currOrderSequence = 0L;
    private long  lastTimeStamp = -1L;
    private long sequenceMask = (1<<22);

    private IDGenerator(){

        GlobalConfig config = (GlobalConfig) UApplicationContext.getBean("globalConfig");

        if(config == null){
            Log.e("GlobalConfig is not exists. deployID not config?");
            return;
        }

        serverID = config.getDeployID();

    }

    public synchronized static IDGenerator getInstance(){
        if(instance == null){
            instance = new IDGenerator();
        }

        return instance;
    }


    public synchronized long nextOrderID(){

        Calendar can = Calendar.getInstance();
        int year = can.get(Calendar.YEAR) - 2013;
        int month = can.get(Calendar.MONTH) + 1;
        int day = can.get(Calendar.DAY_OF_MONTH);
        int hour = can.get(Calendar.HOUR_OF_DAY);
        int min = can.get(Calendar.MINUTE);
        int sec = can.get(Calendar.SECOND);

        long req = year;
        req = req << 4 | month;
        req = req << 5 | day;
        req = req << 5 | hour;
        req = req << 6 | min;
        req = req << 6 | sec;

        if(serverID >= 1024 || serverID < 0){
            Log.e("U8Server deploy_id must be in 0(include) and 1024(exclude)");
            return -1;
        }

        long currTime = req;
        if(req == lastTimeStamp){

            this.currOrderSequence = this.currOrderSequence + 1;
            if(this.currOrderSequence >= sequenceMask){
                this.currOrderSequence = sequenceMask;
                Log.e("WOW!!! u8server had generate more than %s orders per seconds. I'm sure you now have enough money to redevelop u8server to fix the problem", sequenceMask);
                return -1;
            }

        }else{
            this.currOrderSequence = 0L;
            lastTimeStamp = currTime;
        }

        req = req << 10| serverID;
        req = req << 22| this.currOrderSequence;

        return req;
    }
}
