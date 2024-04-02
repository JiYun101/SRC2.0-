package com.msb.crm.controller;

import com.msb.crm.annoation.RequiredPermission;
import com.msb.crm.base.BaseController;
import com.msb.crm.base.ResultInfo;
import com.msb.crm.enums.StateStatus;
import com.msb.crm.query.SaleChanceQuery;
import com.msb.crm.service.SaleChanceService;
import com.msb.crm.utils.CookieUtil;
import com.msb.crm.utils.LoginUserUtil;
import com.msb.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
@RequestMapping("sale_chance")
@Controller
public class SaleChanceController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;

    @RequiredPermission(code = "101001")
    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> querySaleChanceByParams(SaleChanceQuery saleChanceQuery,
                                                      Integer flag,HttpServletRequest request){
        //判断flag的值
        if (flag!=null && flag==1){
            //查询开发计划，设置分配状态
            saleChanceQuery.setState(StateStatus.STATED.getType());
            //设置指派人，当前id
            Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
            saleChanceQuery.setAssignMan(userId);
        }
        Map<String, Object> map = saleChanceService.querySaleChancesByParams(saleChanceQuery);
        return map;
    }

    /**
     * 进入营销机会页面
     * @return
     */
    @RequiredPermission(code = "1010")
    @RequestMapping("index")
    public String index(){
        return "saleChance/sale_chance";
    }

    /**
     * 添加营销机会记录
     * @param saleChance
     * @param request
     * @return
     */
    @RequiredPermission(code = "101002")
    @PostMapping("add")
    @ResponseBody
    public ResultInfo addSaleChance(SaleChance saleChance, HttpServletRequest request){
        //从cookie获取用户名
        String userName= CookieUtil.getCookieValue(request,"userName");
        //设置用户名到对象中
        saleChance.setCreateMan(userName);
        //低啊用service添加方法
        saleChanceService.addSaleChance(saleChance);
        return success("添加成功！");
    }

    /**
     * 更新
     * @param saleChance
     * @return
     */
    @RequiredPermission(code = "101004")
    @PostMapping("update")
    @ResponseBody
    public ResultInfo updateSaleChance(SaleChance saleChance){

        //调用service添加方法
        saleChanceService.updateSaleChance(saleChance);
        return success("更新成功！");
    }

    /**
     * 进入添加或者修改页面
     * @return
     */
    @RequestMapping("toSaleChancePage")
    public String toSaleChancePage(Integer id,HttpServletRequest request){
        if (id!=null){
            SaleChance saleChance=saleChanceService.selectByPrimaryKey(id);
            request.setAttribute("saleChance",saleChance);
        }

        return "saleChance/add_update";
    }

    @RequiredPermission(code = "101003")
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteSaleChance(Integer[] ids){
        //调用service方法
        saleChanceService.deleteSaleChance(ids);
        return success("删除成功！");
    }

    /**
     * 更新开发状态
     * @return
     */
    @PostMapping("updateSaleChanceDevResult")
    @ResponseBody
    public ResultInfo updateSaleChanceDevResult(Integer id,Integer devResult){
        //调用更新方法
        saleChanceService.updateSaleChanceDevResult(id,devResult);
        return success("更新成功！");
    }
}
