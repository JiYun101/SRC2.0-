package com.msb.crm.dao;

import com.msb.crm.base.BaseMapper;
import com.msb.crm.vo.Permission;

import java.util.List;

public interface PermissionMapper extends BaseMapper<Permission,Integer> {

    Integer countPermissionByRoleId(Integer roleId);

    void deletePermissionByRoleId(Integer roleId);

    List<Integer> queryRoleHasModuleIdsByRoleId(Integer roleId);

    List<String> queryUserHasRoleHasPermissionByUserId(Integer userId);

    Integer countPermissionByModuleId(Integer id);

    Integer deletePermissionByModuleId(Integer id);
}