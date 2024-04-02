package com.msb.crm.service;

import com.msb.crm.base.BaseService;
import com.msb.crm.dao.ModuleMapper;
import com.msb.crm.dao.PermissionMapper;
import com.msb.crm.model.TreeModel;
import com.msb.crm.utils.AssertUtil;
import com.msb.crm.vo.Module;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class ModuleService extends BaseService<Module,Integer> {
    @Resource
    private ModuleMapper moduleMapper;

    @Resource
    private PermissionMapper permissionMapper;

        /**
     * 查询指定角色的所有模块资源，并标记已授权的资源
     * @param roleId 角色ID，用于查询该角色已授权的资源
     * @return 返回一个树形结构的资源列表，已授权的资源将被标记为已选中（setChecked为true）
     */
    public List<TreeModel> queryAllModules(Integer roleId){
        List<TreeModel> treeModels = new ArrayList<>();
        try {
            // 确保角色ID有效，防止恶意输入，这里假设已经进行了验证（例如通过注解或前端校验）
            if (roleId == null) {
                // 可以考虑记录日志或抛出一个具体的异常
                throw new IllegalArgumentException("角色ID不能为空");
            }
            // 查询系统中所有的资源模块
            treeModels = moduleMapper.queryAllModules();
            // 根据角色ID查询该角色已授权的资源模块ID列表
            List<Integer> permissionIds = permissionMapper.queryRoleHasModuleIdsByRoleId(roleId);
            // 对授权资源ID列表进行效率优化
            if (!permissionIds.isEmpty()) {
                Set<Integer> permissionIdSet = new HashSet<>(permissionIds);
                // 遍历资源列表，标记已授权的资源
                treeModels.forEach(treeModel -> {
                    if (permissionIdSet.contains(treeModel.getId())) {
                        treeModel.setChecked(true);
                    }
                });
            }
        } catch (Exception e) {
            // 记录异常信息，方便问题追踪
            // log.error("查询资源模块时发生异常", e);
            // 可以根据实际情况决定是否需要对外抛出自定义异常或返回一个错误提示
        }
        return treeModels;
    }



    /**
     * 查询模块列表，并将结果封装成Map返回。
     *
     * @return 返回一个包含模块列表信息的Map对象，其中包含以下键：
     *         - code: 表示操作状态码，0代表成功。
     *         - msg: 表示操作结果的消息，成功时为空字符串。
     *         - count: 表示模块列表的总数。
     *         - data: 表示模块列表的数据。
     */
    public Map<String,Object> queryModuleList(){
        Map<String,Object> map=new HashMap<>();
        // 从moduleMapper中查询模块列表
        List<Module> modules = moduleMapper.queryModuleList();
        // 设置返回结果的初始状态
        map.put("code",0);
        map.put("msg","");
        // 设置模块列表的总数
        map.put("count",modules.size());
        // 将查询到的模块列表放入返回结果中
        map.put("data",modules);
        return map;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void addModule(Module module){
        //参数校验
        //层级grade非空 0-1-2
        Integer grade=module.getGrade();
        AssertUtil.isTrue(null==grade || !(grade==0 || grade==1 || grade ==2),"非法层级！");
        //模块名称非空
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"模块名称不能为空！");
        //模块名称统一层级下模块名称唯一
        AssertUtil.isTrue(null!= moduleMapper.queryModuleByGradeAndModuleName(grade,module.getModuleName()),"模块名称重复！");
        //判断是否二级菜单
        if (grade==1){
            //地址url非空，二级菜单非空
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"URL不能为空！");
            //地址不重复
            AssertUtil.isTrue(null!=moduleMapper.queryModuleByGradeAndUrl(grade, module.getUrl()),"模块地址重复！");
        }
        if (grade==0){
            module.setParentId(-1);
        }
        //父级菜单
        if (grade!=0){
            //非空
            AssertUtil.isTrue(null==module.getParentId(),"父级菜单不能为空！");
            AssertUtil.isTrue(null==moduleMapper.selectByPrimaryKey(module.getParentId()),"请选择正确的父级菜单！");
        }
        //权限码非空
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"权限码不能为空！");
        //权限码不重复
        AssertUtil.isTrue(null!=moduleMapper.queryModuleByOptValue(module.getOptValue()),"权限码重复！");
        //设置默认参数
        module.setIsValid((byte) 1);
        module.setCreateDate(new Date());
        module.setUpdateDate(new Date());
        //执行添加，返回受影响行数
        AssertUtil.isTrue(moduleMapper.insertSelective(module)<1,"添加失败！");
    }

    /**
     * 更新
     * @param module
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateModule(Module module){
        //参数校验
        //id非空，数据有
        AssertUtil.isTrue(null==module.getId(),"记录不存在！");
        //通过id查询资源对象
        Module temp = moduleMapper.selectByPrimaryKey(module.getId());
        //判断是否存在
        AssertUtil.isTrue(null==temp,"记录不存在！");
        //层级非空判断
        Integer grade=module.getGrade();
        AssertUtil.isTrue(null==grade || !(grade==0 || grade==1 || grade ==2),"非法层级！");
        //模块名同一层级下唯一，不包含当前修改记录 非空
        AssertUtil.isTrue(StringUtils.isBlank(module.getModuleName()),"名字不能为空！");
        temp=moduleMapper.queryModuleByGradeAndModuleName(grade,module.getModuleName());
        if (temp!=null){
            AssertUtil.isTrue(!(temp.getId()).equals(module.getId()),"模块名重复！");
        }
        //地址判断
        if (grade==1){
            AssertUtil.isTrue(StringUtils.isBlank(module.getUrl()),"地址不能为空！");
            temp=moduleMapper.queryModuleByGradeAndUrl(grade,module.getUrl());
            if (temp!=null){
                AssertUtil.isTrue(!(temp.getId()).equals(module.getId()),"地址相同！");
            }
        }
        //权限码非空
        AssertUtil.isTrue(StringUtils.isBlank(module.getOptValue()),"权限码不能为空！");
        //通过权限码查询资源对象
        temp=moduleMapper.queryModuleByOptValue(module.getOptValue());
        if (temp!=null){
            AssertUtil.isTrue(!(temp.getId()).equals(module.getId()),"权限码已存在！");
        }
        //设置默认参数
        module.setUpdateDate(new Date());
        //执行更新
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(module)<1,"更新失败！");
    }

    /**
     * 删除权限
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteModule(Integer id) {
        //判断Id是否空
        AssertUtil.isTrue(null==id,"记录不存在！");
        //查询资源对象
        Module temp = moduleMapper.selectByPrimaryKey(id);
        //判断是否为空
        AssertUtil.isTrue(null==temp,"记录不存在！");
        //查询是否有子记录
        Integer count=moduleMapper.queryModuleByParentId(id);
        AssertUtil.isTrue(count>0,"有子记录不可删除！");
        //查询是否存在记录！
        count=permissionMapper.countPermissionByModuleId(id);
        if (count>0){
            //删除指定资源id的权限记录
            permissionMapper.deletePermissionByModuleId(id);
        }
        //设置记录无效
        temp.setIsValid((byte) 0);
        temp.setUpdateDate(new Date());

        //执行更新
        AssertUtil.isTrue(moduleMapper.updateByPrimaryKeySelective(temp)<1,"更新失败！");
    }
}
