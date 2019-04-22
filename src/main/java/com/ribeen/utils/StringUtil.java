package com.ribeen.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串工具类
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/11 11:54
 */
public class StringUtil {
    /**
     * 取匹配结果，无循环匹配
     * @param string  EG: SELECT(.*)FROM
     * @param regex 正则表达式
     * @param num 匹配次数
     * @return String
     */
    public static String matcherThin(String string, String regex, int num) {
        Matcher matcher = Pattern.compile(regex, Pattern.CASE_INSENSITIVE).matcher(string);
        return matcher.find() ? matcher.group(num) : null;
    }

    /**
     * 获得字符串格式的当前时间
     *
     * @return java.lang.String
     */
    public static String getNowString() {
        return new SimpleDateFormat(Constant.DATE_FORMAT).format(new Date());
    }

    /**
     * StringBuilder移除后面几个字符
     *
     * @param str 要移除的字符串
     * @param lastCharCount 移除数量
     * @return java.lang.StringBuilder
     */
    public static String removeLastChar(String str, int lastCharCount) {
        if (str == null || str.length() <= lastCharCount) {
            return "";
        }
        if (lastCharCount < 1) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        int length = sb.length();
        return sb.delete(length - lastCharCount, length).toString();
    }
}
