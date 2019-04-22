package com.ribeen.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * 读取配置文件
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/12 9:03
 */
@Component
public class Properties {
    // 本项目默认数据库连接
    /**
     * 连接地址
     */
    static String dbUrl;
    /**
     * 用户名
     */
    static String dbUsername;
    /**
     * 密码
     */
    static String dbPassword;

    // Redis数据库连接
    /**
     * IP地址
     */
    static String host;
    /**
     * 端口号
     */
    static int port;
    /**
     * 密码
     */
    static String auth;
    /**
     * 使用哪个库
     */
    static int database;
    /**
     * 连接池最大连接数(使用负值表示没有限制)
     */
    static int maxActive;
    /**
     * 最大空闲连接数(默认为8, 负数表示无限)
     */
    static int maxIdle;
    /**
     * 最小空闲连接数(默认为0, 该值只有为正数才有作用)
     */
    static int minIdle;
    /**
     * 连接池最大阻塞等待时间(使用负值表示没有限制)
     */
    static long maxWait;
    /**
     * 连接超时的时间
     */
    static int timeout;

    @Value("${spring.datasource.url}")
    public void setDbUrl(String dbUrl) {
        Properties.dbUrl = dbUrl;
    }

    @Value("${spring.datasource.username}")
    public void setDbUsername(String dbUsername) {
        Properties.dbUsername = dbUsername;
    }

    @Value("${spring.datasource.password}")
    public void setDbPassword(String dbPassword) {
        Properties.dbPassword = dbPassword;
    }

    @Value("${spring.redis.host}")
    public void setHost(String host) {
        Properties.host = host;
    }

    @Value("${spring.redis.port}")
    public void setPort(int port) {
        Properties.port = port;
    }

    @Value("${spring.redis.auth}")
    public void setAuth(String auth) {
        Properties.auth = auth;
    }

    @Value("${spring.redis.database}")
    public void setDatabase(int database) {
        Properties.database = database;
    }

    @Value("${spring.redis.max-active}")
    public void setMaxActive(int maxActive) {
        Properties.maxActive = maxActive;
    }

    @Value("${spring.redis.max-idle}")
    public void setMaxIdle(int maxIdle) {
        Properties.maxIdle = maxIdle;
    }

    @Value("${spring.redis.min-idle}")
    public void setMinIdle(int minIdle) {
        Properties.minIdle = minIdle;
    }

    @Value("${spring.redis.max-wait}")
    public void setMaxWait(long maxWait) {
        Properties.maxWait = maxWait;
    }

    @Value("${spring.redis.timeout}")
    public void setTimeout(int timeout) {
        Properties.timeout = timeout;
    }
}
