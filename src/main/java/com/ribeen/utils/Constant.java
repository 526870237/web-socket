package com.ribeen.utils;

/**
 * 常量
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/11 12:00
 */
public interface Constant {
    /**
     * MySQL驱动
     */
    String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    /**
     * Oracle驱动
     */
    String ORACLE_DRIVER = "oracle.jdbc.driver.OracleDriver";
    /**
     * MySQL
     */
    String MYSQL = "mysql";
    /**
     * ORACLE
     */
    String ORACLE = "oracle";
    /**
     * 默认日期格式
     */
    String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    /**
     * 拼接字符串时字符串之间需要用逗号隔开,并加单引号
     */
    String SQL_COMMA = "', '";
    /**
     * 查询列表分页时的页码
     */
    String PAGE = "page";
    /**
     * 查询列表分页时的每页条数
     */
    String LIMIT = "limit";
    /**
     * 查询列表分页时的数据总数
     */
    String COUNT = "count";
}
