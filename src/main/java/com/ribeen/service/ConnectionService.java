package com.ribeen.service;

import com.ribeen.entity.ConnectionEntity;
import com.ribeen.entity.TableEntity;
import com.ribeen.utils.Dao;
import com.ribeen.utils.Result;
import com.ribeen.utils.StringUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

import static com.ribeen.utils.Constant.MYSQL;
import static com.ribeen.utils.Constant.ORACLE;

/**
 * 数据库连接
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/6 16:13
 */
@Service
public class ConnectionService extends BaseService<ConnectionEntity> {
    {
        // 查询列表以及详情的字段
        columns = "id, name, url, username, update_date";
    }

    /**
     * 获得某个连接的所有表信息
     *
     * @param params 参数: connectionId
     * @return com.ribeen.utils.Result
     */
    public Result getTables(Map<String, String> params) {
        String connectionId = params.get("connectionId");
        ConnectionEntity connectionEntity = new Dao().queryObject(
                "SELECT url, username, password FROM CONNECTION WHERE id = '" + connectionId + "'",
                ConnectionEntity.class);
        String dbType = StringUtil.matcherThin(connectionEntity.getUrl(), "jdbc:(\\S.+?):", 1);
        if (dbType == null) {
            throw new RuntimeException("不支持的数据库类型! ");
        }
        String lowerDbType = dbType.toLowerCase();
        String sql = getQueryTablesSQL(lowerDbType);
        List<TableEntity> tableEntities = new Dao(connectionEntity.getUrl(), connectionEntity.getUsername(),
                connectionEntity.getPassword()).queryList(sql, TableEntity.class);
        return Result.ok(tableEntities);
    }

    /**
     * 查询表的SQL
     *
     * @return java.lang.String
     */
    private String getQueryTablesSQL(String lowerDbType) {
        if (MYSQL.equals(lowerDbType)) {
            return "SELECT " +
                    "table_name name, " +
                    "table_comment comments, " +
                    "create_time create_time " +
                    "FROM " +
                    "information_schema.TABLES " +
                    "WHERE " +
                    "table_schema = (SELECT DATABASE()) " +
                    "ORDER BY create_time DESC ";
        } else if (ORACLE.equals(lowerDbType)) {
            return "SELECT " +
                    "t.table_name name, " +
                    "f.comments comments, " +
                    "o.created create_time " +
                    "FROM " +
                    "user_tables t " +
                    "INNER JOIN user_tab_comments f ON t.table_name = f.table_name " +
                    "LEFT JOIN user_objects o ON f.table_name = o.object_name " +
                    "ORDER BY o.created DESC ";
        } else {
            throw new RuntimeException("暂时只支持MySQL和Oracle数据库! ");
        }
    }

    @Override
    ConnectionEntity queryObject(String id) {
        return new Dao().queryObject("SELECT id, name, url, username, password FROM " + tableName +
                " WHERE id = '" + id + "'", type);
    }

    public Result test(Map<String, String> params) {
        String url = params.get("url");
        String username = params.get("username");
        String password = params.get("password");
        try {
            new Dao(url, username, password).execute("SELECT 1 FROM DUAL");
            return Result.ok();
        } catch (Exception e) {
            return Result.error();
        }
    }
}
