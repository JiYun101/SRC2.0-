package com.msb.crm.query;

import com.msb.crm.base.BaseQuery;
import lombok.Data;

@Data
public class SaleChanceQuery extends BaseQuery {

    //条件查询
    private String customerName;//客户名
    private String createMan;//创建人
    private Integer state;//状态 0未分配 1已分配

    //客户开发条件查询
    private String devResult;
    private Integer assignMan;
    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCreateMan() {
        return createMan;
    }

    public void setCreateMan(String createMan) {
        this.createMan = createMan;
    }

    public Integer getState() {
        return state;
    }

    public void setState(Integer state) {
        this.state = state;
    }
}
