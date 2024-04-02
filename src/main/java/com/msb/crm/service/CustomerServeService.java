package com.msb.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.msb.crm.base.BaseService;
import com.msb.crm.dao.CustomerMapper;
import com.msb.crm.dao.CustomerServeMapper;
import com.msb.crm.dao.UserMapper;
import com.msb.crm.enums.CustomerServeStatus;
import com.msb.crm.query.CustomerServeQuery;
import com.msb.crm.utils.AssertUtil;
import com.msb.crm.vo.Customer;
import com.msb.crm.vo.CustomerServe;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerServeService extends BaseService<CustomerServe ,Integer> {
    @Resource
    private CustomerServeMapper customerServeMapper;
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private UserMapper userMapper;

    public Map<String, Object> queryCustomerServeByParams(CustomerServeQuery customerServeQuery) {

        Map<String, Object> map = new HashMap<String, Object>();
        //得到分页对象
        PageHelper.startPage(customerServeQuery.getPage(), customerServeQuery.getLimit());
        PageInfo<CustomerServe> pageInfo = new PageInfo<>(customerServeMapper.selectByParams(customerServeQuery));
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        //存入分页数据
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 添加服务
     * @param customerServe
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCustomerServe(CustomerServe customerServe) {
        //参数校验
        //客户名非空
        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getCustomer()),"客户名不能为空！");
        //客户表中存在客户记录
        AssertUtil.isTrue(customerMapper.queryCustomerByName(customerServe.getCustomer())==null,"客户不存在！");
        //服务类型非空
        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServeType()),"请选择服务类型！");
        //请求内容不能为空！
        AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceRequest()),"服务请求内容不能为空！");
        //设置默认参数
        customerServe.setState(CustomerServeStatus.CREATED.getState());
        customerServe.setIsValid(1);
        customerServe.setCreateDate(new Date());
        customerServe.setUpdateDate(new Date());
        //执行更新
        AssertUtil.isTrue(customerServeMapper.insertSelective(customerServe)<1,"添加失败！");
    }

    /**
     * 更新客户服务状态
     * @param customerServe
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomerServe(CustomerServe customerServe){
        //客户服务id非空存在
        AssertUtil.isTrue(customerServe.getId()==null || customerServeMapper.selectByPrimaryKey(customerServe.getId())==null,"记录不存在！");
        //判断状态
        if (CustomerServeStatus.ASSIGNED.getState().equals(customerServe.getState())){
            //服务分配操作
            //分配人
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getAssigner()),"分配用户不能为空！");
            AssertUtil.isTrue(userMapper.selectByPrimaryKey(Integer.parseInt(customerServe.getAssigner()))==null,"分配用户不存在！");
            //时间
            customerServe.setAssignTime(new Date());
            //更新
            customerServe.setUpdateDate(new Date());
        }else if (CustomerServeStatus.PROCED.getState().equals(customerServe.getState())){
            //服务处理操作
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceProce()),"服务处理内容不能为空！");
            //时间
            customerServe.setServiceProceTime(new Date());
            customerServe.setUpdateDate(new Date());
        }else if (CustomerServeStatus.FEED_BACK.getState().equals(customerServe.getState())){
            //服务反馈操作
            //反馈内容非空
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getServiceProceResult()),"服务反馈不能为空！");
            //满意度非空
            AssertUtil.isTrue(StringUtils.isBlank(customerServe.getMyd()),"请选择满意度！");
            //状态
            customerServe.setState(CustomerServeStatus.ARCHIVED.getState());
        }
        //更新时间
        customerServe.setUpdateDate(new Date());
        //执行更新
        AssertUtil.isTrue(customerServeMapper.updateByPrimaryKeySelective(customerServe)<1,"更新失败！");
    }
}
