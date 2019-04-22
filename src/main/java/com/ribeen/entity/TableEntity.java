package com.ribeen.entity;

import java.util.Date;

/**
 * 表
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/12 14:16
 */
public class TableEntity {
    /**
     * 表名
     */
    private String name;
    /**
     * 表备注
     */
    private String comments;
    /**
     * 表创建时间
     */
    private Date createTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}
