package com.u8.server.task;

import com.u8.server.data.UOrder;
import com.u8.server.log.Log;


import java.util.concurrent.DelayQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 应用宝SDK相关支付逻辑
 */
public class OrderTaskManager {

    private static final long DELAY_MILLIS = 600000;      //每次延迟执行间隔,ms. 这里是10分钟
    private static final int MAX_RETRY_NUM = 10;         //最多重试10次

    private static OrderTaskManager instance;

    private DelayQueue<OrderTask> tasks;

    private ExecutorService executor;

    private volatile boolean isRunning = false;

    public OrderTaskManager() {
        this.tasks = new DelayQueue<OrderTask>();
        executor = Executors.newFixedThreadPool(3);
    }

    public static OrderTaskManager getInstance() {
        if (instance == null) {
            instance = new OrderTaskManager();
        }
        return instance;
    }


    //添加一个新支付请求到队列中
    public void addOrder(UOrder order) {
        OrderTask task = new OrderTask(order, 1000, MAX_RETRY_NUM);
        this.tasks.add(task);
        if (!isRunning) {
            isRunning = true;
            execute();
        }
    }

    public void execute() {
        executor.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isRunning) {
                        OrderTask task = tasks.take();
                        Log.e("开始补单 :%s", task.getOrder().getOrderID());
                        task.run();
                        if (task.getState() == OrderTask.STATE_RETRY) {
                            task.setDelay(DELAY_MILLIS * task.getRetryCount());
                            tasks.add(task);
                        } else if (task.getState() == OrderTask.STATE_FAILED) {
                            Log.e("补单失败 ：%s", task.getOrder().getOrderID());
                        }
                    }
                } catch (Exception e) {
                    Log.e(e.getMessage(), e);
                }
            }
        });
    }

    public void destory() {
        this.isRunning = false;
        if (executor != null) {
            executor.shutdownNow();
            executor = null;
        }
    }


}
