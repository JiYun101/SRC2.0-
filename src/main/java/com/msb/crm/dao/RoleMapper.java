package com.msb.crm.dao;

import com.msb.crm.base.BaseMapper;
import com.msb.crm.vo.Role;

import java.util.List;
import java.util.Map;


public interface RoleMapper extends BaseMapper<Role,Integer> {

    List<Map<String,Object>> queryAllRoles(Integer userId);

    Role selectByRoleName(String roleName);
}