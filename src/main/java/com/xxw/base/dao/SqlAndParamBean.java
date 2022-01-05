package com.xxw.base.dao;

import com.alibaba.fastjson.JSON;

import java.util.List;

public class SqlAndParamBean {
    private String sql;
    private Object[] params;
    private List<Object[]> batchParams;

    public SqlAndParamBean(String sql, Object[] params, List<Object[]> batchParams) {
        super();
        this.sql = sql;
        this.params = params;
        this.batchParams = batchParams;
    }

    public SqlAndParamBean() {
        super();
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public List<Object[]> getBatchParams() {
        return batchParams;
    }

    public void setBatchParams(List<Object[]> batchParams) {
        this.batchParams = batchParams;
    }

    @Override
    public String toString() {
        return JSON.toJSONString(this);
    }

}
