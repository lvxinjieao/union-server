package com.u8.server.data;

import java.util.concurrent.atomic.AtomicInteger;

public class ULoginInfo {

    private String username;
    private AtomicInteger tryNum;
    private long time;

    public ULoginInfo(){

    }

    public ULoginInfo(String username, int tryNum){
        this.username = username;
        this.tryNum = new AtomicInteger(tryNum);
        this.time = System.currentTimeMillis();
    }

    public int addTryNum(){
        return tryNum.incrementAndGet();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public AtomicInteger getTryNum() {
        return tryNum;
    }

    public void setTryNum(AtomicInteger tryNum) {
        this.tryNum = tryNum;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
}
