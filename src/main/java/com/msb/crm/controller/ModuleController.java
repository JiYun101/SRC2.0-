package com.msb.crm.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONField;
import com.msb.crm.base.BaseController;
import com.msb.crm.base.ResultInfo;
import com.msb.crm.model.TreeModel;
import com.msb.crm.service.ModuleService;
import com.msb.crm.vo.Module;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("module")
public class ModuleController extends BaseController {
    @Resource
    private ModuleService moduleService;



    @RequestMapping("queryAllModules")
    @ResponseBody
    public List<TreeModel> queryAllModules(Integer roleId, HttpServletRequest request){
        request.setAttribute("roleId",roleId);
        List<TreeModel> treeModels = moduleService.queryAllModules(roleId);
        /*String jsonString = JSON.toJSONString(treeModels);*/
        return treeModels;
    }

    /**
     * 进入添加权限页面
     * @param roleId
     * @param request
     * @return
     */
    @RequestMapping("toAddGrantPage")
    public String toAddGrantPage(Integer roleId,HttpServletRequest request){
        request.setAttribute("roleId",roleId);

        return "role/grant";
    }

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryModuleList(){
        return moduleService.queryModuleList();
    }

    @RequestMapping("index")
    public String index(){
        return "module/module";
    }

    /**
     * 添加操作
     * @param module
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo addModule(Module module){
        moduleService.addModule(module);
        return success("添加成功！");
    }

    /**
     * 更新操作
     * @param module
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateModule(Module module){
        moduleService.updateModule(module);
        return success("更新成功！");
    }

    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteModule(Integer id){
        moduleService.deleteModule(id);
        return success("删除成功！");
    }

    /**
     * 打开更新页面
     * @param id
     * @param request
     * @return
     */
    @RequestMapping("toUpdateModulePage")
    public String toUpdateModulePage(Integer id,HttpServletRequest request){
        Module module = moduleService.selectByPrimaryKey(id);
        request.setAttribute("module",module);
        return "module/update";
    }

    /**
     * 打开添加资源页面
     * @return
     */
    @RequestMapping("toAddModulePage")
    public String toAddModulePage(Integer grade,Integer parentId,HttpServletRequest request){
        //数据设置到请求域中
        request.setAttribute("grade",grade);
        request.setAttribute("parentId",parentId);
        return "module/add";
    }
}

