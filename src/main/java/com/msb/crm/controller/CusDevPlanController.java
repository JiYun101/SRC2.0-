package com.msb.crm.controller;

import com.msb.crm.base.BaseController;
import com.msb.crm.base.ResultInfo;
import com.msb.crm.enums.StateStatus;
import com.msb.crm.query.CusDevPlanQuery;
import com.msb.crm.query.SaleChanceQuery;
import com.msb.crm.service.CusDevPlanService;
import com.msb.crm.service.SaleChanceService;
import com.msb.crm.utils.LoginUserUtil;
import com.msb.crm.vo.CusDevPlan;
import com.msb.crm.vo.SaleChance;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequestMapping("cus_dev_plan")
@Controller
public class CusDevPlanController extends BaseController {
    @Resource
    private SaleChanceService saleChanceService;

    @Resource
    private CusDevPlanService cusDevPlanService;

    @RequestMapping("index")
    public String index() {
        return "cusDevPlan/cus_dev_plan";
    }

    @RequestMapping("toCusDevPlanDataPage")
    public String toCusDevPlanDataPage(Integer id, HttpServletRequest request) {
        //通过id查询营销机会对象
        SaleChance saleChance = saleChanceService.selectByPrimaryKey(id);
        System.out.println(saleChance);
        //将对象设置到请求域中
        request.setAttribute("saleChance", saleChance);
        return "CusDevPlan/cus_dev_plan_data";
    }

    /**
     * 客户开发计划数据查询
     *
     * @param
     * @param
     * @param
     * @return
     */
    @RequestMapping("list")
    @ResponseBody
    public Map<String, Object> queryCusDevPlanByParams(CusDevPlanQuery cusDevPlanQuery) {
        //判断flag的值

        Map<String, Object> map = cusDevPlanService.queryCusDevPlanByParams(cusDevPlanQuery);
        System.out.println(map.toString());
        return map;
    }

    /**
     * 添加计划
     *
     * @param cusDevPlan
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo addCusDevPlan(CusDevPlan cusDevPlan) {
        cusDevPlanService.addCusDevPlan(cusDevPlan);
        return success("添加成功！");
    }

    /**
     * 更新计划
     *
     * @param cusDevPlan
     * @return
     */
    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateCusDevPlan(CusDevPlan cusDevPlan) {

        cusDevPlanService.updateCusDevPlan(cusDevPlan);
        return success("更新成功！");
    }
    /**
     * 删除计划项
     *
     * @param id
     * @return
     */
    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteCusDevPlan(Integer id) {

        cusDevPlanService.deleteCusDevPlan(id);
        return success("删除成功！");
    }

    /**
     * 进入添加或者修改页面
     */
    @RequestMapping("addOrUpdateCusDevPlanPage")
    public String addOrUpdateCusDevPlanPage(HttpServletRequest request, Integer sid,Integer id) {

        //把营销机会id设置到请求域中
        request.setAttribute("sid", sid);
        //通过计划项id查询记录
        CusDevPlan cusDevPlan = cusDevPlanService.selectByPrimaryKey(id);
        request.setAttribute("cusDevPlan", cusDevPlan);
        return "cusDevPlan/add_update";
    }


}
