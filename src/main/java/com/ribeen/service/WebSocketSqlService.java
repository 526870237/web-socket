package com.ribeen.service;

import com.ribeen.entity.WebSocketSqlEntity;
import com.ribeen.utils.Dao;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * Sql查询推送至前端
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/6 16:13
 */
@Service
public class WebSocketSqlService extends BaseService<WebSocketSqlEntity> {
    {
        columns = "id, client_group_id, connection_id, query_sql, update_date";
    }

    @Override
    public int doInsert(Map<String, String> params, String id) {
        String connectionId = params.get("connectionId");
        String querySql = params.get("querySql");
        try {
            new Dao(connectionId).queryList(querySql);
        } catch (Exception e) {
            return 0;
        }
        return super.doInsert(params, id);
    }

    @Override
    public int doUpdate(Map<String, String> params) {
        String connectionId = params.get("connectionId");
        String querySql = params.get("querySql");
        try {
            new Dao(connectionId).queryList(querySql);
        } catch (Exception e) {
            return 0;
        }
        return super.doUpdate(params);
    }
}
