package com.ribeen.schedule;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.ribeen.entity.WebSocketEntity;
import com.ribeen.entity.WebSocketSqlEntity;
import com.ribeen.service.WebSocketService;
import com.ribeen.service.WebSocketSqlService;
import com.ribeen.utils.Dao;
import com.ribeen.utils.RedisUtil;
import com.ribeen.utils.StringUtil;
import com.ribeen.utils.WebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.ribeen.utils.WebSocket.formatWebSocketSet;

/**
 * 定时任务
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/13 16:17
 */
@Configuration
@EnableScheduling
@Component
public class ScheduleJobs {
    private final WebSocketService webSocketService;
    private final WebSocketSqlService webSocketSqlService;

    @Autowired
    public ScheduleJobs(WebSocketService webSocketService, WebSocketSqlService webSocketSqlService) {
        this.webSocketService = webSocketService;
        this.webSocketSqlService = webSocketSqlService;
    }

    /**
     * 定时从缓存表中取数据放到Redis中
     */
    @Scheduled(cron = "${spring.schedule.corn.get-by-table}")
    public void getByTable() {
        // 获得所有配置好的web-socket
        List<WebSocketEntity> webSocketEntities = webSocketService.queryList(new HashMap<>(0));
        for (WebSocketEntity webSocketEntity : webSocketEntities) {
            String connectionId = webSocketEntity.getConnectionId();
            String tableName = webSocketEntity.getTableNameOrSql();
            String tempTableName = webSocketEntity.getId().substring(4);
            Dao dao = new Dao(connectionId);
            StringBuilder ids = new StringBuilder();
            StringBuilder dataIdsStr = new StringBuilder();
            StringBuilder dataIds = new StringBuilder();
            synchronized (this) {
                // 查询缓存表数据
                List<Map<String, Object>> maps = dao.queryList("SELECT id, data_id FROM t" +
                        tempTableName + " ORDER BY id");
                for (Map<String, Object> map : maps) {
                    ids.append("'").append(map.get("id")).append("', ");
                    dataIdsStr.append(map.get("id")).append(", ");
                    dataIds.append("'").append(map.get("dataId")).append("', ");
                }
                // 查询完后立即删除, 避免重复数据
                if (ids.length() > 0) {
                    dao.execute("DELETE FROM t" + tempTableName + " WHERE id IN (" +
                            StringUtil.removeLastChar(ids.toString(), 2) + ")");
                }
            }
            // 按照插入顺序查询新增的数据
            if (dataIds.length() > 0) {
                List<Map<String, Object>> mapList = dao.queryList("SELECT * FROM " + tableName +
                        " WHERE id IN (" + StringUtil.removeLastChar(dataIds.toString(), 2) +
                        ") ORDER BY INSTR('" + StringUtil.removeLastChar(dataIdsStr.toString(),
                        2) + "', id)");
                String[] maps = new String[mapList.size()];
                for (int i = 0; i < mapList.size(); i++) {
                    Map<String, Object> map = mapList.get(i);
                    // 序列化时日期格式化
                    maps[i] = JSON.toJSONString(map, SerializerFeature.WriteDateUseDateFormat,
                            SerializerFeature.WriteNullStringAsEmpty);
                }
                // 按先后顺序放到Redis列表中
                RedisUtil.setList(tempTableName, maps);
            }
        }
    }

    /**
     * 定时从Redis中取数据, 通过web-socket向前端推送
     */
    @Scheduled(cron = "${spring.schedule.corn.get-by-redis}")
    public void getByRedis() {
        Map<String, Set<WebSocket>> webSocketSets = formatWebSocketSet();
        List<WebSocketEntity> webSocketEntities = webSocketService.queryList(new HashMap<>(0));
        for (WebSocketEntity webSocketEntity : webSocketEntities) {
            String clientGroupId = webSocketEntity.getClientGroupId();
            Set<WebSocket> webSockets = webSocketSets.get(clientGroupId);
            if (webSockets != null) {
                // 若存在这个web-socket, 则从Redis中取出数据, 推送到前端
                String data = RedisUtil.lpopList(webSocketEntity.getId().substring(4));
                if (data != null) {
                    for (WebSocket webSocket : webSockets) {
                        try {
                            webSocket.sendMessage(data);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    /**
     * 定时sql查询数据数据, 通过web-socket向前端推送
     */
    @Scheduled(cron = "${spring.schedule.corn.get-by-sql}")
    public void getBySql() {
        Map<String, Set<WebSocket>> webSocketSets = formatWebSocketSet();
        List<WebSocketSqlEntity> webSocketEntities = webSocketSqlService.
                queryList(new HashMap<>(0));
        for (WebSocketSqlEntity webSocketSqlEntity : webSocketEntities) {
            String connectionId = webSocketSqlEntity.getConnectionId();
            String querySql = webSocketSqlEntity.getQuerySql();
            String clientGroupId = webSocketSqlEntity.getClientGroupId();
            Set<WebSocket> webSockets = webSocketSets.get(clientGroupId);
            if (webSockets != null) {
                // 若存在这个web-socket, 则通过sql查询数据出数据, 推送到前端
                List<Map<String, Object>> mapList = new Dao(connectionId).queryList(querySql);
                if (mapList.size() > 0) {
                    for (WebSocket webSocket : webSockets) {
                        try {
                            webSocket.sendMessage(JSON.toJSONString(mapList,
                                    SerializerFeature.WriteDateUseDateFormat,
                                    SerializerFeature.WriteNullStringAsEmpty));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
}
