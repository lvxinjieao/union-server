package com.u8.server.common;

import com.u8.server.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.BoundHashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Redis操作抽象
 * Created by ant on 2016/12/18.
 */
public abstract class URedisTemplate<T> {

    private RedisTemplate<String, T> redisTemplate;
    private BoundHashOperations<String, String, T> hashOps;

    @Autowired
    private UDistributiedLock lock;

    @PostConstruct
    private void init(){

        try{
            this.hashOps = redisTemplate.boundHashOps(getTypeKey());
        }catch (Exception e){
            Log.e(e.getMessage(), e);
        }

    }

    public T get(String key){
        try{
            return this.hashOps.get(key);
        }catch (Exception e){
            Log.e(e.getMessage());
            e.printStackTrace();
        }

        return null;

    }

    public List<T> getAll(String key){

        try{
            Map<String,T> entries = this.hashOps.entries();
            List<T> result = new ArrayList<T>();
            for(Map.Entry<String,T> t : entries.entrySet()){
                result.add(t.getValue());
            }
            return result;
        }catch (Exception e){
            Log.e(e.getMessage());
            e.printStackTrace();
        }

        return new ArrayList<T>();

    }

    public void save(T data){
        if(data == null) return;
        try{
            this.hashOps.put(getDataKey(data), data);
        }catch (Exception e){
            Log.e(e.getMessage(), e);
        }


    }

    public void saveInLock(final String lockKey, final T data){
        if(data == null) return;

        doInLock(lockKey, new Runnable() {
            @Override
            public void run() {
                save(data);
            }
        });
    }

    public void saveInLock(final T data){
        if(data == null) return;
        doInLock(data, new Runnable() {
            @Override
            public void run() {
                save(data);
            }
        });
    }

    public void expire(String key, long time, TimeUnit unit){
        try{
            this.redisTemplate.expire(key, time, unit);
        }catch (Exception e){
            Log.e(e.getMessage(), e);
        }

    }

    public void save(String hk, String key, T data){

        if(data == null) return;

        try{

            BoundHashOperations<String,String,T> ops = this.redisTemplate.boundHashOps(hk);
            ops.put(key, data);

        }catch (Exception e){
            Log.e(e.getMessage());
            Log.e(e.getMessage(), e);
        }


    }

    public T get(String hk, String key){

        try{

            BoundHashOperations<String,String,T> ops = this.redisTemplate.boundHashOps(hk);
            return ops.get(key);

        }catch (Exception e){
            Log.e(e.getMessage(), e);
        }
        return null;
    }

    public void delete(String key){

        try{
            this.hashOps.delete(key);
        }catch (Exception e){
            Log.e(e.getMessage());
            e.printStackTrace();
        }

    }


    public void delete(String hk, String key){
        try{
            BoundHashOperations<String,String,T> ops = this.redisTemplate.boundHashOps(hk);
            ops.delete(key);
        }catch (Exception e){
            Log.e(e.getMessage(), e);
        }
    }

    /**
     * 删除整个hash
     */
    public void deleteAll(){
        this.redisTemplate.delete(getTypeKey());
    }

    public void deleteAll(String key){
        this.redisTemplate.delete(key);
    }

    public void delete(T data){
        try{
            String key = getDataKey(data);
            this.hashOps.delete(key);
        }catch (Exception e){
            Log.e(e.getMessage());
            e.printStackTrace();
        }

    }


    @Autowired
    public void setRedisTemplate(@Qualifier("redisTemplate") RedisTemplate<String,T> template)
    {
        this.redisTemplate = template;
    }

    public abstract String getTypeKey();

    public abstract String getDataKey(T data);

    public  String getLockKey(T data){
        return getTypeKey()+getDataKey(data);
    }

    public boolean isLock(T data){

        return lock.isLock(getLockKey(data));
    }

    public boolean doInLock(T data, Runnable runnable){

        String lockKey = getLockKey(data);

        return doInLock(lockKey, runnable);
    }

    public boolean doInLock(String lockKey, Runnable runnable){

        boolean locked = lock.tryLock(lockKey);
        if(locked) {
            runnable.run();
            lock.releaseLock(lockKey);
            return true;
        }else{
            Log.e("do in lock failed. get lock failed. lock key:%s", lockKey);
        }
        return false;
    }

    public boolean doInLockWithRetry(T data, Runnable runnable){

        String lockKey = getLockKey(data);

        return doInLockWithRetry(lockKey, runnable);
    }

    public boolean doInLockWithRetry(String lockKey, Runnable runnable){

        int failedCount = 0;
        do{
            boolean locked = lock.tryLock(lockKey);
            if(locked){
                runnable.run();
                lock.releaseLock(lockKey);
                return true;
            }else{
                failedCount++;
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }while (failedCount < 5);

        Log.e("doInLock failed. retry more than 5 times. but still failed to get lock from redis...");

        return false;
    }
}
