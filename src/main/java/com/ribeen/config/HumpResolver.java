package com.ribeen.config;

import com.ribeen.utils.HumpUtil;
import org.apache.commons.dbutils.RowProcessor;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * MapListHandler和MapHandler字段名转为驼峰
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/10/23 13:58
 */
public class HumpResolver implements RowProcessor {
    @Override
    public Object[] toArray(ResultSet arg0) {
        return null;
    }

    @Override
    public <T> T toBean(ResultSet arg0, Class<? extends T> arg1) {
        return null;
    }

    @Override
    public <T> List<T> toBeanList(ResultSet arg0, Class<? extends T> arg1) {
        return null;
    }

    @Override
    public Map<String, Object> toMap(ResultSet rs) throws SQLException {
        ResultSetMetaData resultSetMetaData = rs.getMetaData();
        int cols = resultSetMetaData.getColumnCount();
        Map<String, Object> result = new HashMap<>(cols);
        for (int i = 1; i <= cols; i++) {
            String columnName = resultSetMetaData.getColumnLabel(i);
            if (null == columnName || 0 == columnName.length()) {
                columnName = resultSetMetaData.getColumnName(i);
            }
            result.put(HumpUtil.toHump(columnName), rs.getObject(i));
        }
        return result;
    }
}
