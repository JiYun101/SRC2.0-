package com.msb.crm.controller;

import com.msb.crm.base.BaseController;
import com.msb.crm.base.ResultInfo;
import com.msb.crm.query.CustomerQuery;
import com.msb.crm.service.CustomerService;
import com.msb.crm.vo.Customer;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@Controller
@RequestMapping("customer")
public class CustomerController extends BaseController {
    @Resource
    private CustomerService customerService;

    @RequestMapping("list")
    @ResponseBody
    public Map<String,Object> querySaleChanceByParams(CustomerQuery customerQuery,
                                                      Integer flag, HttpServletRequest request){
        //判断flag的值

        Map<String, Object> map = customerService.queryCustomerByParams(customerQuery);
        return map;
    }

    /**
     * 进入主页
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "customer/customer";
    }

    /**
     * 打开添加更新页面
     * @param id
     * @param request
     * @return
     */
    @RequestMapping("toAddOrUpdateCustomerPage")
    public String toAddOrUpdateCustomerPage(Integer id,HttpServletRequest request){
        if (id!=null){
            Customer customer = customerService.selectByPrimaryKey(id);
            request.setAttribute("customer",customer);
        }
        return "customer/add_update";
    }

    /**
     * 添加操作
     * @param customer
     * @return
     */
    @RequestMapping("add")
    @ResponseBody
    public ResultInfo addCustomer(Customer customer){
        customerService.addCustomer(customer);
        return success("添加成功！");
    }

    /**
     * 更新操作！
     * @param customer
     * @return
     */
    @ResponseBody
    @RequestMapping("update")
    public ResultInfo updateCustomer(Customer customer){
        customerService.updateCustomer(customer);
        return success("添加成功");
    }

    /**
     * 删除操作
     * @param id
     * @return
     */
    @ResponseBody
    @RequestMapping("delete")
    public ResultInfo deleteCustomer(Integer id){
        customerService.deleteCustomer(id);
        return success("删除成功");
    }

    /**
     * 打开页面
     * @param customerId
     * @param request
     * @return
     */
    @RequestMapping("toCustomerOrderPage")
    public String toCustomerOrderPage(Integer customerId,HttpServletRequest request){
        Customer customer = customerService.selectByPrimaryKey(customerId);
        request.setAttribute("customer",customer);
        return "customer/customer_order";
    }

    /**
     * 客户共享分析
     * @param customerQuery
     * @return
     */
    @ResponseBody
    @RequestMapping("queryCustomerContributionByParams")
    public Map<String,Object> queryCustomerContributionByParams(CustomerQuery customerQuery){

        return customerService.queryCustomerContributionByParams(customerQuery);
    }

    @ResponseBody
    @RequestMapping("countCustomerMake")
    public Map<String,Object> countCustomerMake(){
        return customerService.countCustomerMake();
    }

    @ResponseBody
    @RequestMapping("countCustomerMake02")
    public Map<String,Object> countCustomerMake02(){
        return customerService.countCustomerMake02();
    }
}
