package com.msb.crm.controller;

import com.msb.crm.base.BaseController;
import com.msb.crm.base.ResultInfo;
import com.msb.crm.query.CustomerLossQuery;
import com.msb.crm.service.CustomerLossService;
import com.msb.crm.vo.CustomerLoss;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("customer_loss")
public class CustomerLossController extends BaseController {
    @Resource
    private CustomerLossService customerLossService;

    @RequestMapping("index")
    public String index(){
        return "customerLoss/customer_loss";
    }

    @RequestMapping("list")
    @ResponseBody
    private Map<String,Object> queryCustomerLossByParams(CustomerLossQuery customerLossQuery){
       return customerLossService.queryCustomerLossByParams(customerLossQuery);
    }

    @RequestMapping("toCustomerLossPage")
    public String toCustomerLossPage(Integer id, HttpServletRequest request){
        if(id!=null){
            CustomerLoss customerLoss = customerLossService.selectByPrimaryKey(id);
            request.setAttribute("customerLoss",customerLoss);
        }
        return "customerLoss/customer_rep";
    }

    @RequestMapping("updateCustomerLossStateById")
    @ResponseBody
    public ResultInfo updateCustomerLossStateById(Integer id,String lossReason){
        customerLossService.updateCustomerLossStateById(id,lossReason);
        return success("更新成功！");
    }
}
