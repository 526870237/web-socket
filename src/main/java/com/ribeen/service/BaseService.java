package com.ribeen.service;

import com.ribeen.utils.*;

import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.Map;

import static com.ribeen.utils.Constant.*;

/**
 * 基本的增删改查服务
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/12 16:13
 */
public abstract class BaseService<T> {
    /**
     * 要查询列表或详情的字段名(以英文逗号隔开), 需要在实现类的静态构造块中重写, 否则默认只查询id.
     */
    String columns = "id";

    /**
     * 获得T.class
     */
    @SuppressWarnings("unchecked")
    protected final Class<T> type = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass())
            .getActualTypeArguments()[0];

    /**
     * 表名, 要求表名下划线形式, 示例名大驼峰并以Entity结尾, 如: 表名为test_user, 实例名为TestUserEntity
     * 需要下面的构造代码块赋值, 构造对象时会先执行构造代码块.
     */
    protected String tableName;

    {
        // 根据类名获得表名
        String className = type.getSimpleName();
        className = className.substring(0, 1).toLowerCase() + className.substring(1);
        tableName = HumpUtil.toUnderline(className.substring(0, className.lastIndexOf("Entity")))
                .toUpperCase();
    }

    /**
     * 给Controller用的, 插入数据库, 需要表有id和update_date字段
     *
     * @param params 表中除了id和update_date的其他字段, 需要以驼峰形式, 并且不能传表不存在的字段
     * @return com.ribeen.utils.Result
     */
    public Result insert(Map<String, String> params) {
        int row = doInsert(params);
        if (row == 1) {
            return Result.ok();
        } else {
            return Result.error();
        }
    }

    /**
     * 给Service用的, 执行插入, 需要表有id和update_date字段
     *
     * @param params 表中除了id和update_date的其他字段, 需要以驼峰形式, 并且不能传表不存在的字段
     * @return int
     */
    public int doInsert(Map<String, String> params) {
        return doInsert(params, IDUtil.getId());
    }

    int doInsert(Map<String, String> params, String id) {
        // 将参数由驼峰全都转换成下划线
        params = HumpUtil.mapKeyHumpToUnderline(params);
        StringBuilder headSql = new StringBuilder("INSERT INTO " + tableName + " (id, ");
        StringBuilder tailSql = new StringBuilder("update_date) VALUES ('" + id + SQL_COMMA);
        for (Map.Entry<String, String> param : params.entrySet()) {
            headSql.append(param.getKey()).append(", ");
            tailSql.append(param.getValue()).append(SQL_COMMA);
        }
        return new Dao().execute(headSql.toString() + tailSql + StringUtil.getNowString() + "')");
    }

    /**
     * 给Controller用的, 执行删除
     *
     * @param params ids 可以删除多个, id直接用英文逗号隔开
     * @return com.ribeen.utils.Result
     */
    public Result delete(Map<String, String> params) {
        doDelete(params);
        return Result.ok();
    }

    /**
     * 给Service用的, 逻辑删除, 需要表有id, del_flag
     *
     * @param params ids, 可以删除多个, id直接用英文逗号隔开
     */
    public void doDelete(Map<String, String> params) {
        String[] ids = params.get("ids").split(",");
        StringBuilder idsString = new StringBuilder();
        for (String id : ids) {
            idsString.append("'").append(id.trim()).append("', ");
        }
        new Dao().execute("UPDATE " + tableName + " SET del_flag = '1' WHERE id IN (" +
                StringUtil.removeLastChar(idsString.toString(), 2) + ")");
    }

    /**
     * 给Controller用的, 更新数据, 需要表有id字段
     *
     * @param params id字段以及其他需要更新的字段, 需要以驼峰形式, 并且不能传表不存在的字段
     * @return com.ribeen.utils.Result
     */
    public Result update(Map<String, String> params) {
        int row = doUpdate(params);
        if (row == 1) {
            return Result.ok();
        } else {
            return Result.error();
        }
    }

    /**
     * 给Service用的, 更新数据
     *
     * @param params id字段以及其他需要更新的字段, 需要以驼峰形式, 并且不能传表不存在的字段
     * @return int
     */
    public int doUpdate(Map<String, String> params) {
        // 将参数由驼峰全都转换成下划线
        params = HumpUtil.mapKeyHumpToUnderline(params);
        StringBuilder sql = new StringBuilder("UPDATE " + tableName + " SET ");
        for (Map.Entry<String, String> param : params.entrySet()) {
            sql.append(param.getKey()).append(" = '").append(param.getValue()).append("', ");
        }
        sql = new StringBuilder(StringUtil.removeLastChar(sql.toString(), 2) + " WHERE id = '" +
                params.get("id") + "'");
        return new Dao().execute(sql.toString());
    }

    /**
     * 给Controller用的, 查询详情
     *
     * @param params id
     * @return com.ribeen.utils.Result
     */
    public Result info(Map<String, String> params) {
        return Result.ok(queryObject(params.get("id")));
    }

    /**
     * 给Service用的, 查询详情, 由于每个表的字段都不相同, 所以强制实体类自己实现方法
     *
     * @param id id
     * @return T
     */
    T queryObject(String id) {
        return new Dao().queryObject("SELECT " + columns + " FROM " + tableName + " WHERE id = '" +
                id + "'", type);
    }

    /**
     * 给Controller用的, 查询列表
     *
     * @param params 若需要条件查询, 则从参数中添加
     * @return com.ribeen.utils.Result
     */
    public Result list(Map<String, String> params) {
        return Result.ok(queryList(params)).put(COUNT, getCount());
    }

    /**
     * 给Service用的, 查询列表, 由于每个表的字段都不相同, 所以强制实体类自己实现方法
     *
     * @return java.util.List<T>
     */
    public List<T> queryList(Map<String, String> params) {
        String sql = "SELECT " + columns + " FROM " + tableName + " WHERE del_flag = '0' ORDER BY update_date " +
                "DESC";
        String page = params.get(PAGE);
        String limit = params.get(LIMIT);
        if (page != null && limit != null) {
            int intPage = Integer.parseInt(page);
            int intLimit = Integer.parseInt(limit);
            sql += " LIMIT " + (intPage - 1) * intLimit + ", " + intLimit;
        }
        return new Dao().queryList(sql, type);
    }

    /**
     * 获得总数
     *
     * @return int
     */
    public int getCount() {
        return Integer.parseInt(new Dao().queryObject("SELECT COUNT(id) AS count FROM " + tableName +
                " WHERE del_flag = '0'").get("count").toString());
    }
}
