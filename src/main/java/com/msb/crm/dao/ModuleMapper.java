package com.msb.crm.dao;

import com.msb.crm.base.BaseMapper;
import com.msb.crm.model.TreeModel;
import com.msb.crm.vo.Module;

import java.util.List;

public interface ModuleMapper extends BaseMapper<Module,Integer> {

    List<TreeModel> queryAllModules();

    List<Module> queryModuleList();

    //通过层级与模块名称查询资源对象
    Module queryModuleByGradeAndModuleName(Integer grade, String moduleName);

    //通过层级与url查询资源对象
    Module queryModuleByGradeAndUrl(Integer grade, String url);

    //通过权限码查询资源对象
    Module queryModuleByOptValue(String optValue);

    Integer queryModuleByParentId(Integer id);
}