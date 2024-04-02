package com.msb.crm.query;

import com.msb.crm.base.BaseQuery;

public class CustomerLossQuery extends BaseQuery {
    private String customerNo; //客户编号
    private String customerName;//客户名称

    private String state;

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
}
