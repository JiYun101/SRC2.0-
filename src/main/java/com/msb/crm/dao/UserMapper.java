package com.msb.crm.dao;

import com.msb.crm.base.BaseMapper;
import com.msb.crm.vo.User;

import java.util.List;
import java.util.Map;

public interface UserMapper extends BaseMapper<User,Integer> {
    User  queryUserByUserName(String userName);

    List<Map<String,Object>> queryAllSales();

    List<Map<String, Object>> queryAllCustomerManager();
}