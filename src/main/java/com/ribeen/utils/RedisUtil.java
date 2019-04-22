package com.ribeen.utils;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import redis.clients.jedis.BinaryClient;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Redis工具类
 * 支持存放类型: String, Map<String, String>, List<String>, Set<String>, 如果是其他对象, 则可以序列化为字符串后使用
 * 注意: 同一个key不允许存不同类型, 如果一个key中已经存放了一种类型, 想更换另一种类型, 需要调用update方法, 而不是set方法.
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/10/11 13:51
 */
@Component
public class RedisUtil {
    /**
     * Redis连接池
     */
    private static JedisPool jedisPool;

    static {
        try {
            JedisPoolConfig config = new JedisPoolConfig();
            config.setMaxTotal(Properties.maxActive);
            config.setMaxIdle(Properties.maxIdle);
            config.setMinIdle(Properties.minIdle);
            config.setMaxWaitMillis(Properties.maxWait);
            jedisPool = new JedisPool(config, Properties.host, Properties.port, Properties.timeout,
                    StringUtils.isBlank(Properties.auth) ? null : Properties.auth, Properties.database);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**X
     * 存字符串, 会替换旧的value
     *
     * @param key   Redis的键
     * @param value Redis的值
     */
    public static void setString(String key, String value) {
        Jedis jedis = jedisPool.getResource();
        jedis.set(key, value);
        jedis.close();
    }

    /**
     * 取字符串
     *
     * @param key Redis的键
     * @return java.lang.String
     */
    public static String getString(String key) {
        Jedis jedis = jedisPool.getResource();
        String value = jedis.get(key);
        jedis.close();
        return value;
    }

    /**
     * 存Map的一个键值对, 会在旧value的基础上追加
     *
     * @param key      Redis的键
     * @param mapKey   map的键
     * @param mapValue map的值
     */
    public static void setSubmap(String key, String mapKey, String mapValue) {
        Jedis jedis = jedisPool.getResource();
        jedis.hset(key, mapKey, mapValue);
        jedis.close();
    }

    /**
     * 存整个Map, 会在旧value的基础上追加
     *
     * @param key Redis的键
     * @param map 存入的map
     */
    public static void setMap(String key, Map<String, String> map) {
        Jedis jedis = jedisPool.getResource();
        jedis.hmset(key, map);
        jedis.close();
    }

    /**
     * 更新Map, 会替换旧的value
     *
     * @param key   Redis的键
     * @param value Redis的值
     */
    public static void updateMap(String key, Map<String, String> value) {
        Jedis jedis = jedisPool.getResource();
        jedis.del(key);
        jedis.hmset(key, value);
        jedis.close();
    }

    /**
     * 取Map的部分值
     *
     * @param key    Redis的键
     * @param mapKey map的键
     * @return java.util.List<java.lang.String>
     */
    public static List<String> getSubmap(String key, String... mapKey) {
        if (mapKey.length == 0) {
            return new ArrayList<>();
        }
        Jedis jedis = jedisPool.getResource();
        List<String> list = jedis.hmget(key, mapKey);
        jedis.close();
        return list;
    }

    /**
     * 取整个Map
     *
     * @param key Redis的键
     * @return java.util.Map<java.lang.String, java.lang.String>
     */
    public static Map<String, String> getMap(String key) {
        Jedis jedis = jedisPool.getResource();
        Map<String, String> map = jedis.hgetAll(key);
        jedis.close();
        return map;
    }

    /**
     * 获得Map长度
     *
     * @param key Redis的键
     * @return long
     */
    public static long getMapLength(String key) {
        Jedis jedis = jedisPool.getResource();
        long length = jedis.hlen(key);
        jedis.close();
        return length;
    }

    /**
     * 获得Map的所有Key
     *
     * @param key Redis的键
     * @return java.util.List<java.lang.String>
     */
    public static List<String> getMapKeys(String key) {
        Jedis jedis = jedisPool.getResource();
        List<String> mapKeys = new ArrayList<>(jedis.hkeys(key));
        jedis.close();
        return mapKeys;
    }

    /**
     * 判断Map中是否某个键
     *
     * @param key    Redis的键
     * @param mapKey map的键
     * @return boolean
     */
    public static boolean mapHasKey(String key, String mapKey) {
        Jedis jedis = jedisPool.getResource();
        boolean hasValue = jedis.hexists(key, mapKey);
        jedis.close();
        return hasValue;
    }

    /**
     * 删除Map的部分键值对
     *
     * @param key    Redis的键
     * @param mapKey map的键
     */
    public static void deleteSubmap(String key, String... mapKey) {
        Jedis jedis = jedisPool.getResource();
        jedis.hdel(key, mapKey);
        jedis.close();
    }

    /**
     * 从右往左存List, 会在旧value的基础上追加
     *
     * @param key       Redis的键
     * @param listValue list的值
     */
    public static void setList(String key, String... listValue) {
        Jedis jedis = jedisPool.getResource();
        jedis.rpush(key, listValue);
        jedis.close();
    }

    /**
     * 更新List(从右往左存), 会替换旧的value
     *
     * @param key   Redis的键
     * @param value Redis的值
     */
    public static void updateList(String key, String... value) {
        Jedis jedis = jedisPool.getResource();
        jedis.del(key);
        jedis.rpush(key, value);
        jedis.close();
    }

    /**
     * 在左边第一个中间元素的左或右插入一个新的元素
     *
     * @param key       Redis的键
     * @param where     BEFORE: 左边, AFTER: 右边
     * @param pivot     中间元素
     * @param listValue 要插入的元素
     */
    public static void insertList(String key, BinaryClient.LIST_POSITION where, String pivot, String listValue) {
        Jedis jedis = jedisPool.getResource();
        jedis.linsert(key, where, pivot, listValue);
        jedis.close();
    }

    /**
     * 取List中最左边的值, 并将这个值从List中删除
     *
     * @param key Redis的键
     * @return java.lang.String
     */
    public static String lpopList(String key) {
        Jedis jedis = jedisPool.getResource();
        String listValue = jedis.lpop(key);
        jedis.close();
        return listValue;
    }

    /**
     * 取List中最右边的值, 并将这个值从List中删除
     *
     * @param key Redis的键
     * @return java.lang.String
     */
    public static String rpopList(String key) {
        Jedis jedis = jedisPool.getResource();
        String listValue = jedis.rpop(key);
        jedis.close();
        return listValue;
    }

    /**
     * 根据下标获得List元素
     *
     * @param key   Redis的键
     * @param index 下标
     * @return java.lang.String
     */
    public static String getListValue(String key, long index) {
        Jedis jedis = jedisPool.getResource();
        String listValue = jedis.lindex(key, index);
        jedis.close();
        return listValue;
    }

    /**
     * 取部分List
     *
     * @param key   Redis的键
     * @param start 开始下标(包含)
     * @param end   结束下标(包含)
     * @return java.util.List<java.lang.String>
     */
    public static List<String> getSubist(String key, int start, int end) {
        Jedis jedis = jedisPool.getResource();
        List<String> sublist = jedis.lrange(key, start, end);
        jedis.close();
        return sublist;
    }

    /**
     * 取整个List
     *
     * @param key Redis的键
     * @return java.util.List<java.lang.String>
     */
    public static List<String> getList(String key) {
        Jedis jedis = jedisPool.getResource();
        List<String> list = jedis.lrange(key, 0, -1);
        jedis.close();
        return list;
    }

    /**
     * 获得List长度
     *
     * @param key Redis的键
     * @return long
     */
    public static long getListLength(String key) {
        Jedis jedis = jedisPool.getResource();
        long length = jedis.llen(key);
        jedis.close();
        return length;
    }

    /**
     * 给List排序, 并返回排序后结果, 但是Redis中仍是排序前状态
     *
     * @param key Redis的键
     * @return void
     */
    public static List<String> sortList(String key) {
        Jedis jedis = jedisPool.getResource();
        List<String> sortedList = jedis.sort(key);
        jedis.close();
        return sortedList;
    }

    /**
     * 从左到右依次删除某个值
     *
     * @param key       Redis的键
     * @param count     删除次数
     * @param listValue 要删除的值
     */
    public static void deleteSubList(String key, long count, String listValue) {
        Jedis jedis = jedisPool.getResource();
        jedis.lrem(key, count, listValue);
        jedis.close();
    }

    /**
     * 存Set, 会在旧value的基础上追加
     *
     * @param key      Redis的键
     * @param setValue set的值
     */
    public static void setSet(String key, String... setValue) {
        Jedis jedis = jedisPool.getResource();
        jedis.sadd(key, setValue);
        jedis.close();
    }

    /**
     * 更新Set, 会替换旧的value
     *
     * @param key      Redis的键
     * @param setValue set的值
     */
    public static void updateSet(String key, String... setValue) {
        Jedis jedis = jedisPool.getResource();
        jedis.del(key);
        jedis.sadd(key, setValue);
        jedis.close();
    }

    /**
     * 从Set中随机取出一个元素, 并将这个值从Set中删除
     *
     * @param key Redis的键
     * @return java.lang.String
     */
    public static String popSetValue(String key) {
        Jedis jedis = jedisPool.getResource();
        String setValue = jedis.spop(key);
        jedis.close();
        return setValue;
    }

    /**
     * 从Set中随机取出多个元素, 并将这些值从Set中删除
     *
     * @param key   Redis的键
     * @param count 数量
     * @return java.util.Set<java.lang.String>
     */
    public static Set<String> popSubset(String key, long count) {
        Jedis jedis = jedisPool.getResource();
        Set<String> subSet = jedis.spop(key, count);
        jedis.close();
        return subSet;
    }

    /**
     * 从Set中随机取出一个元素, 但不将这个值从Set中删除
     *
     * @param key Redis的键
     * @return java.lang.String
     */
    public static String getSetValue(String key) {
        Jedis jedis = jedisPool.getResource();
        String setValue = jedis.srandmember(key);
        jedis.close();
        return setValue;
    }

    /**
     * 从Set中随机取出多个元素, 但不将这些值从Set中删除
     *
     * @param key   Redis的键
     * @param count 数量
     * @return java.util.Set<java.lang.String>
     */
    public static List<String> getSubset(String key, int count) {
        Jedis jedis = jedisPool.getResource();
        List<String> subSet = jedis.srandmember(key, count);
        jedis.close();
        return subSet;
    }

    /**
     * 取整个Set
     *
     * @param key Redis的键
     * @return java.util.Set<java.lang.String>
     */
    public static Set<String> getSet(String key) {
        Jedis jedis = jedisPool.getResource();
        Set<String> set = jedis.smembers(key);
        jedis.close();
        return set;
    }

    /**
     * 获得Set元素个数
     *
     * @param key Redis的键
     * @return long
     */
    public static long getSetCount(String key) {
        Jedis jedis = jedisPool.getResource();
        long count = jedis.scard(key);
        jedis.close();
        return count;
    }

    /**
     * 判断Set中是否某个值
     *
     * @param key      Redis的键
     * @param setValue set的值
     * @return boolean
     */
    public static boolean setHasValue(String key, String setValue) {
        Jedis jedis = jedisPool.getResource();
        boolean hasValue = jedis.sismember(key, setValue);
        jedis.close();
        return hasValue;
    }

    /**
     * 删除Set中部分值
     *
     * @param key      Redis的键
     * @param setValue set的值
     */
    public static void deleteSubset(String key, String... setValue) {
        Jedis jedis = jedisPool.getResource();
        jedis.srem(key, setValue);
        jedis.close();
    }

    /**
     * 删除某个key的缓存
     *
     * @param key Redis的键
     */
    public static void deleteAll(String... key) {
        Jedis jedis = jedisPool.getResource();
        jedis.del(key);
        jedis.close();
    }

    /**
     * 删除一个数据库缓存
     */
    public static void flushDb() {
        Jedis jedis = jedisPool.getResource();
        jedis.flushDB();
        jedis.close();
    }

    /**
     * 设置某个键的过期时间
     *
     * @param key    键
     * @param second 过期时间, 单位: 秒
     */
    public static void outdate(String key, int second) {
        Jedis jedis = jedisPool.getResource();
        jedis.expire(key, second);
        jedis.close();
    }
}