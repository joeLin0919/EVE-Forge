package org.eveforge.service;

import java.util.concurrent.TimeUnit;

/**
 * Redis服务接口
 */
public interface IRedisService {

    /**
     * 设置字符串值
     * @param key 键
     * @param value 值
     */
    void setString(String key, Object value);

    /**
     * 设置字符串值并设置过期时间
     * @param key 键
     * @param value 值
     * @param timeout 过期时间
     * @param unit 时间单位
     */
    void setString(String key, Object value, long timeout, TimeUnit unit);

    /**
     * 获取字符串值
     * @param key 键
     * @return 值
     */
    Object getString(String key);

    /**
     * 删除键值对
     * @param key 键
     * @return 是否删除成功
     */
    Boolean delete(String key);

    /**
     * 设置过期时间
     * @param key 键
     * @param timeout 过期时间
     * @param unit 时间单位
     * @return 是否设置成功
     */
    Boolean expire(String key, long timeout, TimeUnit unit);

    /**
     * 判断键是否存在
     * @param key 键
     * @return 是否存在
     */
    Boolean hasKey(String key);
}