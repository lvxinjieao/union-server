package com.u8.server.sdk.ysdk;

import com.u8.server.constants.PayState;
import com.u8.server.data.UOrder;
import com.u8.server.data.UUser;
import com.u8.server.log.Log;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 应用宝SDK相关支付逻辑
 * Created by ant on 2015/10/14.
 */
//@Component("ysdkManager")
//@Scope("singleton")
public class YSDKManager {

    private static final long DELAY_MILLIS = 20000;      //每次延迟执行间隔,ms
    private static final int MAX_RETRY_NUM = 6;         //最多重试6次

    private static YSDKManager instance;

    private DelayQueue<PayTask> tasks;

    private ExecutorService executor;

    private volatile boolean isRunning = false;

    public YSDKManager(){
        this.tasks = new DelayQueue<PayTask>();
        executor = Executors.newCachedThreadPool();
    }

    public static YSDKManager getInstance(){
        if(instance == null){
            instance = new YSDKManager();
        }
        return instance;
    }


    /***
     * 获取当前指定用户的队列中所有支付任务中游戏币的总和
     */
    public int getTotalCoinNum(long userID){

        int coinNum = 0;
        for(PayTask task : this.tasks){
            if(task.getPayRequest().getUser().getId() == userID && (task.getState() == PayTask.STATE_INIT || task.getState() == PayTask.STATE_RETRY) ){
                coinNum += task.getPayRequest().getCoinNum();
            }
        }

        return coinNum;
    }


    //添加一个新支付请求到队列中
    public void addPayRequest(PayRequest req){

        PayTask task = new PayTask(req, 100, MAX_RETRY_NUM);
        this.tasks.add(task);

        Log.d("add a new pay task.curr num in queue."+this.tasks.size());

        if(!isRunning){
            isRunning = true;
            execute();
        }
    }

    public void execute(){
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try{

                    while(isRunning){
                        PayTask task = tasks.take();
                        task.run();
                        if(task.getState() == PayTask.STATE_RETRY){
                            task.setDelay(DELAY_MILLIS * task.getRetryCount());
                            tasks.add(task);
                        }else if(task.getState() == PayTask.STATE_FAILED){
                            Log.e("the user[%s](channel userID:%s) charge failed.", task.getPayRequest().getUser().getId(), task.getPayRequest().getUser().getChannelUserID());
                        }
                    }

                }catch (Exception e){
                    Log.e(e.getMessage());
                    Log.e(e.getMessage(), e);
                }
            }
        });
    }

    public void destory(){
        this.isRunning = false;
        if(executor != null){
            executor.shutdown();
            executor = null;
        }
    }

}
