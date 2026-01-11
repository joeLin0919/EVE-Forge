package org.eveforge.service.impl;

import org.eveforge.service.IRedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

/**
 * Redis服务实现类
 */
@Service
public class RedisServiceImpl implements IRedisService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Override
    public void setString(String key, Object value) {
        redisTemplate.opsForValue().set(key, value);
    }

    @Override
    public void setString(String key, Object value, long timeout, TimeUnit unit) {
        redisTemplate.opsForValue().set(key, value, timeout, unit);
    }

    @Override
    public Object getString(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public Boolean delete(String key) {
        return redisTemplate.delete(key);
    }

    @Override
    public Boolean expire(String key, long timeout, TimeUnit unit) {
        return redisTemplate.expire(key, timeout, unit);
    }

    @Override
    public Boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }
}