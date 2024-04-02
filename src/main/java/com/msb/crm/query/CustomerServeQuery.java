package com.msb.crm.query;

import com.msb.crm.base.BaseQuery;

public class CustomerServeQuery extends BaseQuery {
    private String customer;//客户名
    private Integer serveType;//服务类型
    private String state;//服务状态 创建001 分配002 处理003 反馈004 归档005


    private Integer assigner;

    public Integer getAssigner() {
        return assigner;
    }

    public void setAssigner(Integer assigner) {
        this.assigner = assigner;
    }

    public String getCustomer() {
        return customer;
    }

    public void setCustomer(String customer) {
        this.customer = customer;
    }

    public Integer getServeType() {
        return serveType;
    }

    public void setServeType(Integer serveType) {
        this.serveType = serveType;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
