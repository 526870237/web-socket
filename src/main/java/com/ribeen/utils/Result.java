package com.ribeen.utils;

import java.util.HashMap;
import java.util.Map;

/**
 * Request响应信息
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/10/9 14:04
 */
public class Result extends HashMap<String, Object> {
    private static final long serialVersionUID = 1L;

    /**
     *
     * 构造函数.
     */
    private Result() {
        put("code", 1);
    }

    /**
     * 响应未知异常.
     *
     * @return com.ribeen.utils.Result
     */
    public static Result error() {
        return error(0, "未知异常，请联系管理员");
    }

    /**
     * 响应自定义异常信息.
     *
     * @param data 错误数据
     * @return com.ribeen.utils.Result
     */
    public static Result error(Object data) {
        return error(0, data);
    }

    /**
     * 响应自定义异常信息和状态码.
     *
     * @param code 状态码
     * @param data 错误信息
     * @return com.ribeen.utils.Result
     */
    public static Result error(int code, Object data) {
        Result result = new Result();
        result.put("code", code);
        result.put("data", data);
        return result;
    }

    /**
     * 响应信息移除指定键值对.
     *
     * @param key 键
     * @return com.ribeen.utils.Result
     */
    public Result remove(String key){
        super.remove(key);
        return this;
    }

    /**
     * 响应成功并自定义数据.
     *
     * @param data 成功数据
     * @return com.ribeen.utils.Result
     */
    public static Result ok(Object data) {
        Result result = new Result();
        result.put("data", data);
        return result;
    }

    /**
     * 响应成功并加入一些键值对.
     *
     * @param map 键值对
     * @return com.ribeen.utils.Result
     */
    public static Result ok(Map<String, Object> map) {
        Result result = new Result();
        result.putAll(map);
        return result;
    }

    /**
     * 响应成功.
     *
     * @return com.ribeen.utils.Result
     */
    public static Result ok() {
        return new Result();
    }

    /**
     * 响应中加入一个键值对.
     *
     * @param key 键
     * @param value 值
     * @return com.ribeen.utils.Result
     */
    @Override
    public Result put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}
