package com.msb.crm.controller;

import com.msb.crm.base.BaseController;
import com.msb.crm.base.ResultInfo;
import com.msb.crm.query.CustomerReprieveQuery;
import com.msb.crm.service.CustomerReprieveService;
import com.msb.crm.vo.CustomerReprieve;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("customer_rep")
public class CustomerReprieveController extends BaseController {
    @Resource
    private CustomerReprieveService customerReprieveService;

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryCustomerReprieveByParams(CustomerReprieveQuery customerReprieveQuery){
       return customerReprieveService.queryCustomerReprieveByParams(customerReprieveQuery);
    }

    @RequestMapping("add")
    @ResponseBody
    public ResultInfo addCustomerRep(CustomerReprieve customerReprieve){
        customerReprieveService.addCustomerRep(customerReprieve);
        return success("添加成功");
    }

    @RequestMapping("update")
    @ResponseBody
    public ResultInfo updateCustomerRep(CustomerReprieve customerReprieve){
        customerReprieveService.updateCustomerRep(customerReprieve);
        return success("修改成功");
    }

    @RequestMapping("addOrUpdateCustomerReprPage")
    public String addOrUpdateCustomerReprPage(Integer lossId, HttpServletRequest request,Integer id){
        request.setAttribute("lossId",lossId);
        if (id!=null){
            CustomerReprieve customerReprieve = customerReprieveService.selectByPrimaryKey(id);
            request.setAttribute("customerRep",customerReprieve);
        }
        return "customerLoss/customer_rep_add_update";
    }

    @RequestMapping("delete")
    @ResponseBody
    public ResultInfo deleteCustomerRepr(Integer id){
        customerReprieveService.deleteCustomerRepr(id);
        return success("删除成功！");
    }
}
