package com.msb.crm.query;

import com.msb.crm.base.BaseQuery;

public class CustomerOrderQuery extends BaseQuery {
    public Integer customerId;

    public Integer getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Integer customerId) {
        this.customerId = customerId;
    }
}
