package com.u8.server.utils;

import java.util.concurrent.atomic.AtomicInteger;

/***
 * 这个是一个简单的唯一ID生成器
 * 在这个应用中，我们需要生成appID, channelID，orderID等
 */
public class IDGenerator {

    private static IDGenerator instance;

    private AtomicInteger currAppID;
    private AtomicInteger currMasterID;
    private AtomicInteger currChannelID;

    private IDGenerator(){

    }

    public synchronized static IDGenerator getInstance(){
        if(instance == null){
            instance = new IDGenerator();
        }

        return instance;
    }

    public void loadCurrMaxIDs(int maxGameID, int maxMasterID, int maxChannelID){

        this.currAppID = new AtomicInteger(maxGameID);

        this.currMasterID = new AtomicInteger(maxMasterID);

        this.currChannelID = new AtomicInteger(maxChannelID);
    }

    public int nextAppID(){


        return this.currAppID.incrementAndGet();
    }

    public int nextMasterID(){
        return this.currMasterID.incrementAndGet();
    }
}
