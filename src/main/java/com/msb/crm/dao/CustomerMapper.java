package com.msb.crm.dao;

import com.msb.crm.base.BaseMapper;
import com.msb.crm.query.CustomerQuery;
import com.msb.crm.vo.Customer;

import java.util.List;
import java.util.Map;

public interface CustomerMapper extends BaseMapper<Customer,Integer> {

    Customer queryCustomerByName(String name);

    /**
     * 查询待流失的数据
     * @return
     */
    List<Customer> queryLossCustomers();

    /**
     * 通过客户id批量更新客户流失状态
     * @param lossCustomerId
     * @return
     */
    int updateCustomerStateByIds(List<Integer> lossCustomerId);

    List<Map<String,Object>> queryCustomerContributionByParams(CustomerQuery customerQuery);

    List<Map<String,Object>> countCustomerMake();


}