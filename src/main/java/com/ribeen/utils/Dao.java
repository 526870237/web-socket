package com.ribeen.utils;

import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.ribeen.config.HumpResolver;
import com.ribeen.entity.ConnectionEntity;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.dbutils.BasicRowProcessor;
import org.apache.commons.dbutils.GenerousBeanProcessor;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.RowProcessor;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import java.beans.PropertyVetoException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ribeen.utils.Constant.MYSQL;
import static com.ribeen.utils.Constant.ORACLE;

/**
 * 数据库访问
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/11 11:34
 */
@Slf4j
public class Dao {
    private final static String URL_REGEX = "jdbc:(\\S.+?):";

    /**
     * 所有的数据库连接池
     */
    private static Map<String, ComboPooledDataSource> allPools = new HashMap<>();

    private QueryRunner queryRunner;

    /**
     * 开启在BeanHandler和BeanListHandler中下划线->驼峰转换所用
     */
    private RowProcessor processor = new BasicRowProcessor(new GenerousBeanProcessor());

    /**
     * 创建queryRunner
     */
    public Dao() {
        // 从配置文件获取默认数据库
        this(Properties.dbUrl, Properties.dbUsername, Properties.dbPassword);
    }

    /**
     * 根据数据库连接Id创建queryRunner
     */
    public Dao(String connectionId) {
        // 从表中获取数据库参数
        ConnectionEntity connectionEntity = new Dao().queryObject(
                "SELECT url, username, password FROM CONNECTION WHERE id = '" + connectionId + "'",
                ConnectionEntity.class);
        queryRunner = new QueryRunner(getDataSource(connectionEntity.getUrl(), connectionEntity.getUsername(),
                connectionEntity.getPassword()));
    }

    /**
     * 创建动态queryRunner
     *
     * @param url      数据库访问路径
     * @param username 用户名
     * @param password 密码
     */
    public Dao(String url, String username, String password) {
        queryRunner = new QueryRunner(getDataSource(url, username, password));
    }

    /**
     * 数据库连接池, url, username, password三者的组合创建一个唯一连接池
     *
     * @param url      数据库访问路径
     * @param username 用户名
     * @param password 密码
     * @return ComboPooledDataSource
     */
    private ComboPooledDataSource getDataSource(String url, String username, String password) {
        String key = url + username + password;
        ComboPooledDataSource comboPooledDataSource = allPools.get(key);
        if (comboPooledDataSource != null) {
            return comboPooledDataSource;
        }
        String dbType = getDbType(url);
        String driver;
        if (MYSQL.equals(dbType)) {
            driver = Constant.MYSQL_DRIVER;
        } else if (ORACLE.equals(dbType)) {
            driver = Constant.ORACLE_DRIVER;
        } else {
            throw new RuntimeException("不支持的数据库类型! ");
        }
        // 创建连接池核心工具类
        comboPooledDataSource = new ComboPooledDataSource();
        try {
            comboPooledDataSource.setDriverClass(driver);
        } catch (PropertyVetoException e) {
            e.printStackTrace();
        }
        comboPooledDataSource.setJdbcUrl(url);
        comboPooledDataSource.setUser(username);
        comboPooledDataSource.setPassword(password);
        comboPooledDataSource.setMinPoolSize(0);
        comboPooledDataSource.setMaxPoolSize(9);
        // 初始化连接数
        comboPooledDataSource.setInitialPoolSize(3);
        // 连接数的增量
        comboPooledDataSource.setAcquireIncrement(3);
        comboPooledDataSource.setCheckoutTimeout(5000);
        comboPooledDataSource.setUnreturnedConnectionTimeout(100);
        comboPooledDataSource.setMaxIdleTime(20);
        comboPooledDataSource.setPreferredTestQuery("SELECT 1");
        allPools.put(key, comboPooledDataSource);
        return comboPooledDataSource;
    }

    public static String getDbType(String url) {
        // 可直接通过数据连接得到数据库类型
        String dbType = StringUtil.matcherThin(url, URL_REGEX, 1);
        if (dbType == null) {
            throw new RuntimeException("不支持的数据库类型! ");
        }
        return dbType.toLowerCase();
    }

    public <T> List<T> queryList(String sql, Class<T> type) {
        log.info("SQL: " + sql);
        try {
            return queryRunner.query(sql, new BeanListHandler<>(type, processor));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("SQL语句有错误! ");
        }
    }

    public List<Map<String, Object>> queryList(String sql) {
        log.info("SQL: " + sql);
        try {
            return queryRunner.query(sql, new MapListHandler(new HumpResolver()));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("SQL语句有错误! ");
        }
    }

    public <T> T queryObject(String sql, Class<T> type) {
        log.info("SQL: " + sql);
        try {
            return queryRunner.query(sql, new BeanHandler<>(type, processor));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("SQL语句有错误! ");
        }
    }

    public Map<String, Object> queryObject(String sql) {
        log.info("SQL: " + sql);
        try {
            return queryRunner.query(sql, new MapHandler(new HumpResolver()));
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("SQL语句有错误! ");
        }
    }

    public int execute(String sql) {
        log.info("SQL: " + sql);
        try {
            return queryRunner.execute(sql);
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("SQL语句有错误! ");
        }
    }
}
