package com.ribeen.utils;

import java.util.UUID;

/**
 * 获得UUID
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/12 16:08
 */
public class IDUtil {
    /**
     * UUID
     *
     * @return java.lang.String
     */
    public static String getId() {
        return UUID.randomUUID().toString().replace("-", "");
    }
}
