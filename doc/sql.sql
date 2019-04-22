/*
Navicat MySQL Data Transfer

Source Server         : 本地mysql
Source Server Version : 50720
Source Host           : 127.0.0.1:3306
Source Database       : web_socket

Target Server Type    : MYSQL
Target Server Version : 50720
File Encoding         : 65001

Date: 2019-04-22 14:00:44
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for connection
-- ----------------------------
DROP TABLE IF EXISTS `connection`;
CREATE TABLE `connection` (
  `id` varchar(32) NOT NULL COMMENT 'UUID',
  `name` varchar(255) DEFAULT NULL COMMENT '数据库连接名称',
  `url` varchar(255) DEFAULT NULL COMMENT '完整地址, 如: jdbc:oracle:thin:@127.0.0.1:1521/orcl',
  `username` varchar(255) DEFAULT NULL COMMENT '用户名',
  `password` varchar(255) DEFAULT NULL COMMENT '密码',
  `update_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='数据库连接';

-- ----------------------------
-- Table structure for web_socket
-- ----------------------------
DROP TABLE IF EXISTS `web_socket`;
CREATE TABLE `web_socket` (
  `id` varchar(32) NOT NULL COMMENT 'UUID',
  `client_group_id` varchar(255) DEFAULT NULL COMMENT '客户端组ID',
  `connection_id` varchar(32) DEFAULT NULL COMMENT '数据库连接id',
  `table_name` varchar(255) DEFAULT NULL COMMENT '表名',
  `update_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='web-socket定时推送前端新增数据';

-- ----------------------------
-- Table structure for web_socket_sql
-- ----------------------------
DROP TABLE IF EXISTS `web_socket_sql`;
CREATE TABLE `web_socket_sql` (
  `id` varchar(32) NOT NULL COMMENT 'UUID',
  `client_group_id` varchar(255) DEFAULT NULL COMMENT '客户端组ID',
  `connection_id` varchar(32) DEFAULT NULL COMMENT '数据库连接id',
  `query_sql` text COMMENT '查询的SQL语句',
  `update_date` timestamp NULL DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `del_flag` char(1) NOT NULL DEFAULT '0' COMMENT '删除标志',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='web-socket定时推送前端Sql查询数据';
