package com.msb.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.msb.crm.base.BaseService;
import com.msb.crm.dao.CustomerLossMapper;
import com.msb.crm.query.CustomerLossQuery;
import com.msb.crm.utils.AssertUtil;
import com.msb.crm.vo.Customer;
import com.msb.crm.vo.CustomerLoss;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerLossService extends BaseService<CustomerLoss,Integer> {
    @Resource
    private CustomerLossMapper customerLossMapper;

    /**
     * 分页条件查询
     * @param customerLossQuery
     * @return
     */
    public Map<String, Object> queryCustomerLossByParams(CustomerLossQuery customerLossQuery) {
        //开启分页
        Map<String, Object> map = new HashMap<String, Object>();
        //得到分页对象
        PageHelper.startPage(customerLossQuery.getPage(), customerLossQuery.getLimit());
        PageInfo<CustomerLoss> pageInfo = new PageInfo<>(customerLossMapper.selectByParams(customerLossQuery));
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        //存入分页数据
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 更新流失状态
     * @param id
     * @param lossReason
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomerLossStateById(Integer id, String lossReason) {
        //参数校验，Id非空
        AssertUtil.isTrue(null==id,"客户不存在！");
        //查询记录
        CustomerLoss customerLoss = customerLossMapper.selectByPrimaryKey(id);
        //判断是否存在
        AssertUtil.isTrue(null==customerLoss,"客户不存在！");
        //设置默认值
        customerLoss.setState(1);
        customerLoss.setConfirmLossTime(new Date());
        customerLoss.setUpdateDate(new Date());
        customerLoss.setLossReason(lossReason);
        //执行更新
        AssertUtil.isTrue(customerLossMapper.updateByPrimaryKeySelective(customerLoss)<1,"更新失败！");
    }
}
