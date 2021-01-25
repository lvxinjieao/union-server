package com.u8.server.common;

import com.u8.server.config.GlobalConfig;
import com.u8.server.log.Log;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import java.util.Collections;

/**
 * 基于redis的分布式锁
 */
@Component
public class UDistributiedLock {
    private static final long DEFAULT_LOCK_TIME_PX = 500;
    private static final String LOCK_KEY_PREFIX = "ulock-";
    private static final String LOCK_SUCCESS = "OK";
    private static final String SET_IF_NOT_EXIST = "NX";
    private static final String SET_WITH_EXPIRE_TIME = "PX";
    private static final String RELEASE_LOCK_SCRIPT = "if redis.call('get', KEYS[1]) == ARGV[1] then return redis.call('del', KEYS[1]) else return 0 end";

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private GlobalConfig globalConfig;

    public boolean tryLock(final String lockKey) {

        return tryLock(lockKey, globalConfig.getDeployID() + "");
    }

    public boolean tryLock(final String lockKey, final String lockValue) {

        return tryLock(lockKey, lockValue, DEFAULT_LOCK_TIME_PX);
    }

    public boolean tryLock(final String lockKey, final String lockValue, final long milliSeconds) {

        final String key = LOCK_KEY_PREFIX + lockKey;

        return redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                try {

                    Jedis jedis = (Jedis) redisConnection.getNativeConnection();
                    String result = jedis.set(key, lockValue, SET_IF_NOT_EXIST, SET_WITH_EXPIRE_TIME, milliSeconds);

                    if (LOCK_SUCCESS.equals(result)) {
                        return true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("tryLock in redis failed. " + e.getMessage(), e);
                }
                return false;
            }
        });
    }

    public boolean isLock(final String lockKey) {
        String key = LOCK_KEY_PREFIX + lockKey;
        return redisTemplate.hasKey(key);
    }

    public boolean releaseLock(final String lockKey) {
        return releaseLock(lockKey, globalConfig.getDeployID() + "");
    }

    public boolean releaseLock(final String lockKey, final String value) {
        final String key = LOCK_KEY_PREFIX + lockKey;
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            @Override
            public Boolean doInRedis(RedisConnection redisConnection) throws DataAccessException {
                try {

                    Jedis jedis = (Jedis) redisConnection.getNativeConnection();
                    Object result = jedis.eval(RELEASE_LOCK_SCRIPT, Collections.singletonList(key), Collections.singletonList(value));

                    if (LOCK_SUCCESS.equals(result)) {
                        return true;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Log.e("releaseLock in redis failed. " + e.getMessage(), e);
                }
                return false;
            }
        });
    }

}
