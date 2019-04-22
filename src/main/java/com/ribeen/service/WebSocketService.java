package com.ribeen.service;

import com.ribeen.entity.ConnectionEntity;
import com.ribeen.entity.WebSocketEntity;
import com.ribeen.entity.WebSocketSqlEntity;
import com.ribeen.utils.Dao;
import com.ribeen.utils.IDUtil;
import com.ribeen.utils.Result;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.ribeen.utils.Constant.*;

/**
 * web-socket
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/13 11:39
 */
@Service
public class WebSocketService extends BaseService<WebSocketEntity> {
    private final ConnectionService connectionService;
    private final WebSocketSqlService webSocketSqlService;

    /**
     * 入库触发
     */
    private static final String TYPE0 = "0";
    /**
     * 定时查询
     */
    private static final String TYPE1 = "1";

    @Autowired
    public WebSocketService(ConnectionService connectionService, WebSocketSqlService webSocketSqlService) {
        this.connectionService = connectionService;
        this.webSocketSqlService = webSocketSqlService;
    }

    {
        columns = "id, client_group_id, connection_id, table_name, update_date";
    }

    /**
     * http://127.0.0.1:8080/websocket/insert?ipPort=127.0.0.1:8080
     * &connectionId=14f697e21db646fbb04ea450a6f07831&tableName=connection
     *
     * @param params 参数
     * @return int
     */
    @Override
    public int doInsert(Map<String, String> params) {
        String type = params.get("type");
        params.remove("type");
        String newId = IDUtil.getId();
        int row = 0;
        if (TYPE0.equals(type)) {
            params.remove("querySql");
            // 为目标表创建触发器和缓存表, 当有新增数据时, 将触发器会将新增数据信息放到缓存表中
            String connectionId = params.get("connectionId");
            ConnectionEntity connectionEntity = connectionService.queryObject(connectionId);
            String dbType = Dao.getDbType(connectionEntity.getUrl());
            // 监听表名
            String tableName = params.get("tableName");
            // 部分缓存表表名
            String tempTableName = newId.substring(4);
            // 创建缓存表Sql
            String createTempSql;
            // 创建触发器Sql
            String createTriggerSql;
            Dao dao = new Dao(connectionId);
            if (MYSQL.equals(dbType)) {
                createTempSql = "CREATE TABLE t" + tempTableName + " ( " +
                        "id int NOT NULL AUTO_INCREMENT, " +
                        "data_id varchar(225) NULL, " +
                        "PRIMARY KEY (id)) ";
                createTriggerSql = "CREATE TRIGGER t_" + tempTableName +
                        " AFTER INSERT ON " + tableName +
                        " FOR EACH ROW" +
                        " INSERT INTO t" + tempTableName +
                        " (data_id)" +
                        " VALUES (NEW.id)";
            } else if (ORACLE.equals(dbType)) {
                // 如果是oracle, 则还要创建序列
                String createSequenceSql = "CREATE SEQUENCE s_" + tempTableName +
                        " minvalue 1 maxvalue 99999999" +
                        " increment by 1" +
                        " start with 1";
                dao.execute(createSequenceSql);
                createTempSql = "CREATE TABLE t" + tempTableName + " ( " +
                        "ID NUMBER PRIMARY KEY, " +
                        "DATA_ID VARCHAR2(255) NULL)";
                createTriggerSql = "BEGIN EXECUTE IMMEDIATE 'CREATE OR REPLACE TRIGGER t_" + tempTableName +
                        " AFTER INSERT ON " + tableName +
                        " FOR EACH ROW BEGIN" +
                        " INSERT INTO t" + tempTableName +
                        " (ID, DATA_ID)" +
                        " VALUES (s_" + tempTableName + ".nextval, :NEW.ID);" +
                        " END;';END;";
            } else {
                throw new RuntimeException("不支持的数据库类型! ");
            }
            dao.execute(createTempSql);
            dao.execute(createTriggerSql);

            // 存入本项目数据库中
            row = super.doInsert(params, newId);
        } else if (TYPE1.equals(type)) {
            params.remove("tableName");
            row = webSocketSqlService.doInsert(params, newId);
        }
        return row;
    }

    /**
     * http://127.0.0.1:8080/websocket/delete?id=f98c00d559ff4790bb2d250bfadc9b8c
     *
     * @param params 参数
     */
    @Override
    public void doDelete(Map<String, String> params) {
        String ids0 = params.get("ids0");
        String ids1 = params.get("ids1");

        if (StringUtils.isNotBlank(ids1)) {
            // 删除webSocketSql数据
            HashMap<String, String> ids1Param = new HashMap<>(1);
            ids1Param.put("ids", ids1);
            webSocketSqlService.doDelete(ids1Param);
        }

        if (StringUtils.isNotBlank(ids0)) {
            // 删除webSocket数据
            HashMap<String, String> ids0Param = new HashMap<>(1);
            ids0Param.put("ids", ids0);
            super.doDelete(ids0Param);

            // 挨个删除相关触发器, 缓存表, 序列
            String[] ids = ids0.split(",");
            for (String id : ids) {
                WebSocketEntity webSocketEntity = queryObject(id.trim());
                if (webSocketEntity != null) {
                    String connectionId = webSocketEntity.getConnectionId();
                    ConnectionEntity connectionEntity = connectionService.queryObject(connectionId);
                    // 部分缓存表表名
                    String tempTableName = webSocketEntity.getId().substring(4);
                    Dao dao = new Dao(connectionId);
                    // 删除触发器
                    dao.execute("DROP TRIGGER t_" + tempTableName + "");
                    // 删除缓存表
                    dao.execute("DROP TABLE t" + tempTableName + "");
                    // 若是Oracle, 则再删除序列
                    if (ORACLE.equals(Dao.getDbType(connectionEntity.getUrl()))) {
                        dao.execute("DROP SEQUENCE s_" + tempTableName + "");
                    }
                }
            }
        }
    }

    /**
     * 这里的修改情况特殊, 实质为先删除后新增, 所以修改时需要传递的参数除了传id, 其他和新增时传递的参数一致, 不能少
     * http://127.0.0.1:8080/websocket/update?id=f726812d821d4bfd88b03010aa2acfc3
     * &ipPort=127.0.0.1:8080&connectionId=14f697e21db646fbb04ea450a6f07831&tableName=connection
     *
     * @param params 参数
     * @return int
     */
    @Override
    public int doUpdate(Map<String, String> params) {
        String type = params.get("type");
        String id = params.get("id");
        int row = 0;
        WebSocketSqlEntity webSocketSqlEntity = webSocketSqlService.queryObject(id);
        WebSocketEntity webSocketEntity = queryObject(id);
        if (webSocketSqlEntity != null) {
            if (TYPE0.equals(type)) {
                params.remove("id");
                params.remove("querySql");
                // 删webSocketSql
                HashMap<String, String> deleteParams = new HashMap<>(1);
                deleteParams.put("ids1", id);
                doDelete(deleteParams);
                // 新增webSocket
                row = doInsert(params);
            } else if (TYPE1.equals(type)) {
                // 更新webSocketSql
                params.remove("type");
                params.remove("tableName");
                row = webSocketSqlService.doUpdate(params);
            }
        } else if (webSocketEntity != null) {
            params.remove("id");
            // 删webSocket
            HashMap<String, String> deleteParams = new HashMap<>(1);
            deleteParams.put("ids0", id);
            doDelete(deleteParams);
            if (TYPE0.equals(type)) {
                // 新增webSocket
                params.remove("querySql");
            } else if (TYPE1.equals(type)) {
                // 新增webSocketSql
                params.remove("tableName");
            }
            row = doInsert(params);
        }
        return row;
    }

    @Override
    public Result info(Map<String, String> params) {
        String id = params.get("id");
        String type = params.get("type");
        Object data;
        if (TYPE0.equals(type)) {
            data = new Dao().queryObject("SELECT id, client_group_id, connection_id, " +
                    "table_name tableNameOrSql, '0' type FROM " + tableName +
                    " WHERE id = '" + id + "'", this.type);
        } else if (TYPE1.equals(type)) {
            data = new Dao().queryObject("SELECT id, client_group_id, connection_id, " +
                    "query_sql tableNameOrSql, '1' type FROM " +
                    webSocketSqlService.tableName + " WHERE id = '" + id + "'", this.type);
        } else {
            return Result.error();
        }
        return Result.ok(data);
    }

    @Override
    public List<WebSocketEntity> queryList(Map<String, String> params) {
        String sql = "SELECT t.* FROM (SELECT '入库触发' type, w.id, w.client_group_id, w.connection_id, c.name connectionName, " +
                "w.table_name tableNameOrSql, w.update_date FROM " + tableName + " w LEFT JOIN " +
                connectionService.tableName + " c ON w.connection_id = c.id WHERE w.del_flag = '0' UNION " +
                "SELECT '定时查询' type, s.id, s.client_group_id, s.connection_id, co.name connectionName, s.query_sql " +
                "tableNameOrSql, s.update_date FROM " + webSocketSqlService.tableName + " s LEFT JOIN " +
                connectionService.tableName + " co ON s.connection_id = co.id WHERE " +
                "s.del_flag = '0') t ORDER BY t.update_date DESC";
        String page = params.get(PAGE);
        String limit = params.get(LIMIT);
        if (page != null && limit != null) {
            int intPage = Integer.parseInt(page);
            int intLimit = Integer.parseInt(limit);
            sql += " LIMIT " + (intPage - 1) * intLimit + ", " + intLimit;
        }
        return new Dao().queryList(sql, type);
    }

    @Override
    public int getCount() {
        String sql = "SELECT (w.count + s.count) AS count FROM ( SELECT COUNT(id) AS count FROM " + tableName +
                " WHERE del_flag = '0' ) w, ( SELECT COUNT(id) AS count FROM " + webSocketSqlService.tableName +
                " WHERE del_flag = '0' ) s";
        return Integer.parseInt(new Dao().queryObject(sql).get("count").toString());
    }
}
