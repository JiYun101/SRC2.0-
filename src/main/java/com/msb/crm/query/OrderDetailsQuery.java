package com.msb.crm.query;

import com.msb.crm.base.BaseQuery;

public class OrderDetailsQuery extends BaseQuery {
    public Integer orderId;//订单id

    public Integer getOrderId() {
        return orderId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }
}
