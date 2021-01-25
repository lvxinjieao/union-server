package com.u8.server.common;

import com.u8.server.log.Log;
import com.u8.server.utils.StringUtils;
import com.u8.server.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.BoundSetOperations;
import org.springframework.data.redis.core.BoundValueOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class UAnalyticsRedisTemplate {

    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private JedisConnectionFactory connectionFactory;

    @PostConstruct
    public void init() {
        stringRedisTemplate = new StringRedisTemplate(connectionFactory);
    }

    /**
     * 计数
     *
     * @param key
     * @param val
     * @param expiredInSecs
     */
    public void increment(String key, long val, long expiredInSecs) {

        try {
            BoundValueOperations vo = stringRedisTemplate.boundValueOps(key);
            vo.increment(val);
            vo.expire(expiredInSecs, TimeUnit.SECONDS);
        } catch (Exception e) {
            Log.e("increment failed with exception:" + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 当天有效的计数
     *
     * @param key
     * @param val
     */
    public void incrementToday(String key, long val) {

        try {
            Date expiredDate = TimeUtils.dateBegin(TimeUtils.nextDay());
            BoundValueOperations vo = stringRedisTemplate.boundValueOps(key);
            vo.increment(val);
            vo.expireAt(expiredDate);
        } catch (Exception e) {
            Log.e("incrementToday failed with exception:" + e.getMessage());
            e.printStackTrace();
        }

    }

    /**
     * 获取指定key对应的计数
     *
     * @param key
     * @return
     */
    public long getNum(String key) {

        try {
            String val = stringRedisTemplate.boundValueOps(key).get();

            if (StringUtils.isEmpty(val)) {
                return 0L;
            }

            return Long.valueOf(val);

        } catch (Exception e) {
            Log.e("getNum in UAnalyticsRedisTemplate failed with exception:" + e.getMessage());
            e.printStackTrace();
        }

        return 0L;
    }

    /**
     * 添加val到键为key的set集合中
     *
     * @param key
     * @param val
     * @param expiredInSecs
     */
    public void sadd(String key, String val, long expiredInSecs) {

        try {

            BoundSetOperations so = stringRedisTemplate.boundSetOps(key);
            so.add(val);
            so.expire(expiredInSecs, TimeUnit.SECONDS);

        } catch (Exception e) {
            Log.e("sadd in UAnalyticsRedisTemplate failed with exception:" + e.getMessage());
            e.printStackTrace();
        }


    }

    /**
     * 添加val到键为key的当天有效的set集合中
     *
     * @param key
     * @param val
     */
    public void saddToday(String key, String val) {

        try {
            Date expiredDate = TimeUtils.dateBegin(TimeUtils.nextDay());

            BoundSetOperations so = stringRedisTemplate.boundSetOps(key);
            so.add(val);
            so.expireAt(expiredDate);

        } catch (Exception e) {
            Log.e("saddToday in UAnalyticsRedisTemplate failed with exception:" + e.getMessage());
            e.printStackTrace();
        }


    }

    /**
     * 获取键为key的set集合中元素的个数
     *
     * @param key
     * @return
     */
    public long scount(String key) {

        try {

            BoundSetOperations so = stringRedisTemplate.boundSetOps(key);
            return so.size();

        } catch (Exception e) {
            Log.e("scount in UAnalyticsRedisTemplate failed with exception:" + e.getMessage());
            e.printStackTrace();
        }

        return 0L;

    }
}
