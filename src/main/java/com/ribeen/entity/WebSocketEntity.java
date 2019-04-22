package com.ribeen.entity;

import java.util.Date;

/**
 * web-socket定时推送前端新增数据
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/13 11:35
 */
public class WebSocketEntity {
    private String id;
    private String type;
    private String clientGroupId;
    private String connectionId;
    private String connectionName;
    private String tableName;
    private String tableNameOrSql;
    private Date updateDate;
    private String delFlag;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getTableNameOrSql() {
        return tableNameOrSql;
    }

    public void setTableNameOrSql(String tableNameOrSql) {
        this.tableNameOrSql = tableNameOrSql;
    }

    public String getConnectionName() {
        return connectionName;
    }

    public void setConnectionName(String connectionName) {
        this.connectionName = connectionName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClientGroupId() {
        return clientGroupId;
    }

    public void setClientGroupId(String clientGroupId) {
        this.clientGroupId = clientGroupId;
    }

    public String getConnectionId() {
        return connectionId;
    }

    public void setConnectionId(String connectionId) {
        this.connectionId = connectionId;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }

    public String getDelFlag() {
        return delFlag;
    }

    public void setDelFlag(String delFlag) {
        this.delFlag = delFlag;
    }
}
