package com.msb.crm.controller;

import com.msb.crm.base.BaseController;
import com.msb.crm.base.ResultInfo;
import com.msb.crm.enums.CustomerServeStatus;
import com.msb.crm.query.CustomerServeQuery;
import com.msb.crm.service.CustomerServeService;
import com.msb.crm.service.CustomerService;
import com.msb.crm.utils.LoginUserUtil;
import com.msb.crm.vo.CustomerServe;
import com.msb.crm.vo.Module;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("customer_serve")
public class CustomerServeController extends BaseController {
    @Resource
    private CustomerServeService customerServeService;

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryCustomerServeByParams(CustomerServeQuery customerServeQuery,Integer flag,HttpServletRequest request){
        if (flag!=null && flag==1){
            customerServeQuery.setAssigner(LoginUserUtil.releaseUserIdFromCookie(request));
        }
        return customerServeService.queryCustomerServeByParams(customerServeQuery);
    }

    @RequestMapping("index/{type}")
    public String index(@PathVariable Integer type){
        //判断类型是否为空
        if (type!=null){
            if (type==1){
                //服务创建
                return "customerServe/customer_serve";
            }else if (type==2){
                return "customerServe/customer_serve_assign";
            }else if (type==3){
                return "customerServe/customer_serve_proce";
            }else if (type==4){
                return "customerServe/customer_serve_feed_back";
            }else if (type==5){
                return "customerServe/customer_serve_archive";
            }
        }else {
            return "";
        }
        return "";
    }

    @RequestMapping("openAddCustomerServePage")
    public String openAddCustomerServePage(){
        return "customerServe/customer_serve_add";
    }

    @RequestMapping("add")
    @ResponseBody
    public ResultInfo addCustomerServe(CustomerServe customerServe){
        customerServeService.addCustomerServe(customerServe);
        return success("添加成功！");
    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateCustomerServe(CustomerServe customerServe){
        customerServeService.updateCustomerServe(customerServe);
        return success("更新成功！");
    }

    /**
     * 打开服务分配页面
     * @return
     */
    @RequestMapping("addCustomerServeAssignPage")
    public String addCustomerServeAssignPage(Integer id, HttpServletRequest request){
        CustomerServe customerServe = customerServeService.selectByPrimaryKey(id);
        request.setAttribute("customerServe",customerServe);
        //model.addAttribute("customerServe",customerServeService.selectByPrimaryKey(id));
        return "customerServe/customer_serve_assign_add";
    }

    /**
     * 进入服务处理页面
     * @param id
     * @param request
     * @return
     */
    @RequestMapping("addCustomerServeProcePage")
    public String addCustomerServeProcePage(Integer id,HttpServletRequest request){
        CustomerServe customerServe = customerServeService.selectByPrimaryKey(id);
        request.setAttribute("customerServe",customerServe);
        return "customerServe/customer_serve_proce_add";
    }

    /**
     * 打开服务反馈页面
     * @param id
     * @param request
     * @return
     */
    @RequestMapping("addCustomerServeBackPage")
    public String addCustomerServeBackPage(Integer id,HttpServletRequest request){
        CustomerServe customerServe = customerServeService.selectByPrimaryKey(id);
        request.setAttribute("customerServe",customerServe);
        return "customerServe/customer_serve_feed_back_add";
    }
}
