package com.msb.crm.controller;

import com.msb.crm.base.BaseController;
import com.msb.crm.query.CustomerOrderQuery;
import com.msb.crm.service.CustomerOrderService;
import com.msb.crm.vo.CustomerOrder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RequestMapping("order")
@Controller
public class CustomerOrderController extends BaseController {
    @Resource
    private CustomerOrderService customerOrderService;

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> queryCustomerOrderByParams(CustomerOrderQuery customerOrderQuery){

        return customerOrderService.queryCustomerOrderByParams(customerOrderQuery);
    }

    /**
     * 打开订单详情的页面
     * @param orderId
     * @param request
     * @return
     */
    @RequestMapping("orderDetailPage")
    public String index(Integer orderId, HttpServletRequest request){
        if (orderId!=null){
            Map<String,Object> map= customerOrderService.queryOrderById(orderId);
            request.setAttribute("order",map);
        }
        return "customer/customer_order_detail";
    }
}
