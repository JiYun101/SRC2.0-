package com.msb.crm.service;

import com.msb.crm.base.BaseService;
import com.msb.crm.dao.ModuleMapper;
import com.msb.crm.dao.PermissionMapper;
import com.msb.crm.dao.RoleMapper;
import com.msb.crm.utils.AssertUtil;
import com.msb.crm.vo.Permission;
import com.msb.crm.vo.Role;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
@Service
public class RoleService extends BaseService<Role,Integer> {
    @Resource
    private RoleMapper roleMapper;

    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private PermissionMapper permissionMapper;

    /**
     * 查询所有的角色列表
     * @return
     */
    public List<Map<String,Object>> queryAllRoles(Integer userId){
        return roleMapper.queryAllRoles(userId);
    }

    /**
     * 添加角色
     * @param role
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addRole(Role role){
        //参数校验
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名字不能为空！");
        //通过角色名查询角色记录
        Role temp=roleMapper.selectByRoleName(role.getRoleName());
        AssertUtil.isTrue(temp!=null,"角色名称存在！");
        //设置默认值
        role.setIsValid(1);
        role.setCreateDate(new Date());
        role.setUpdateDate(new Date());
        //执行添加，判断受影响行数
        AssertUtil.isTrue(roleMapper.insertSelective(role)<1,"添加失败！");
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public void updateRole(Role role){
        //参数校验
        //角色id非空且存在
        AssertUtil.isTrue(null==role.getId(),"记录不存在！");
        //通关角色id查询记录
        Role temp=roleMapper.selectByPrimaryKey(role.getId());
        //判断角色是否存在
        AssertUtil.isTrue(null==temp,"记录不存在！");
        //角色名非空，名称唯一
        AssertUtil.isTrue(StringUtils.isBlank(role.getRoleName()),"角色名称不能为空！");
        //通过名称查询记录
        temp=roleMapper.selectByRoleName(role.getRoleName());
        AssertUtil.isTrue(null!=temp&&(!temp.getId().equals(role.getId())),"角色名称已存在！");
        //设置默认参数
        role.setUpdateDate(new Date());
        //执行更新
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)<1,"更新失败！");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteRole(Integer roleId){
        //判断id是否存在
        AssertUtil.isTrue(null==roleId,"记录不存在！");
        //查询角色
        Role role = roleMapper.selectByPrimaryKey(roleId);
        //判断是否存在
        AssertUtil.isTrue(null==role,"记录不存在！");
        //删除角色设置状态
        role.setIsValid(0);
        role.setUpdateDate(new Date());
        //执行更新
        AssertUtil.isTrue(roleMapper.updateByPrimaryKeySelective(role)<1,"更新失败！");
    }
    /**
     * 添加权限
     * @param roleId
     * @param mIds
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addGrant(Integer roleId,Integer[] mIds){
        //通过角色id查询权限记录
        Integer count=permissionMapper.countPermissionByRoleId(roleId);
        //如果记录存在，删除对应的权限记录
        if (count>0){
            //删除
            permissionMapper.deletePermissionByRoleId(roleId);
        }
        //如果有权限，测添加
        if (mIds!=null && mIds.length>0){
            //定义permission集合
            List<Permission> permissionList=new ArrayList<>();
            //遍历资源id数组
            for (Integer mid:mIds){
                Permission permission=new Permission();
                permission.setModuleId(mid);
                permission.setRoleId(roleId);
                permission.setAclValue(moduleMapper.selectByPrimaryKey(mid).getOptValue());
                permission.setCreateDate(new Date());
                permission.setUpdateDate(new Date());
                //将对象设置到集合中
                permissionList.add(permission);
            }
            //执行添加
            AssertUtil.isTrue(permissionMapper.insertBatch(permissionList)!=permissionList.size(),"添加失败！");
        }
    }
}
