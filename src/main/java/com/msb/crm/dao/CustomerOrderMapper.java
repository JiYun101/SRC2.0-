package com.msb.crm.dao;

import com.msb.crm.base.BaseMapper;
import com.msb.crm.vo.CustomerOrder;

import java.util.Map;

public interface CustomerOrderMapper extends BaseMapper<CustomerOrder,Integer> {

    Map<String, Object> queryOrderById(Integer orderId);

    /**
     * 通过客户id查询最后一条订单记录
     * @param customerId
     * @return
     */
    CustomerOrder queryLossCustomerOrderByCustomerId(Integer customerId);
}