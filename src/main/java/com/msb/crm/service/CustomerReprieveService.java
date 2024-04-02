package com.msb.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.msb.crm.base.BaseService;
import com.msb.crm.base.ResultInfo;
import com.msb.crm.dao.CustomerLossMapper;
import com.msb.crm.dao.CustomerReprieveMapper;
import com.msb.crm.query.CustomerReprieveQuery;
import com.msb.crm.utils.AssertUtil;
import com.msb.crm.vo.CustomerOrder;
import com.msb.crm.vo.CustomerReprieve;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerReprieveService extends BaseService<CustomerReprieve,Integer> {
    @Resource
    private CustomerReprieveMapper customerReprieveMapper;
    @Resource
    private CustomerLossMapper customerLossMapper;

    /**
     * 按照分页查询
     * @param customerReprieveQuery
     * @return
     */
    public Map<String, Object> queryCustomerReprieveByParams(CustomerReprieveQuery customerReprieveQuery) {
        //开启分页
        Map<String, Object> map = new HashMap<String, Object>();
        //得到分页对象
        PageHelper.startPage(customerReprieveQuery.getPage(), customerReprieveQuery.getLimit());
        PageInfo<CustomerReprieve> pageInfo = new PageInfo<>(customerReprieveMapper.selectByParams(customerReprieveQuery));
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        //存入分页数据
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 添加
     * @param customerReprieve
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCustomerRep(CustomerReprieve customerReprieve) {
        //参数校验
        checkParams(customerReprieve.getLossId(),customerReprieve.getMeasure());
        //设置默认值
        customerReprieve.setIsValid(1);
        customerReprieve.setCreateDate(new Date());
        customerReprieve.setUpdateDate(new Date());
        //执行添加
        AssertUtil.isTrue(customerReprieveMapper.insertSelective(customerReprieve)<1,"添加失败！");
        
    }

    /**
     * 参数校验封装
     * @param lossId
     * @param measure
     */
    private void checkParams(Integer lossId, String measure) {
        //Id非空
        AssertUtil.isTrue(null==lossId || customerLossMapper.selectByPrimaryKey(lossId)==null,"ID暂未查询到");
        //内容非空
        AssertUtil.isTrue(StringUtils.isBlank(measure),"暂缓措施内容不能为空！");

    }

    /**
     * 修改
     * @param customerReprieve
     * @return
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomerRep(CustomerReprieve customerReprieve) {
        //Id非空
        AssertUtil.isTrue(null==customerReprieve.getId() || customerLossMapper.selectByPrimaryKey(customerReprieve.getId())==null,"ID暂未查询到");
        //参数校验
        checkParams(customerReprieve.getLossId(),customerReprieve.getMeasure());
        //设置默认值
        customerReprieve.setUpdateDate(new Date());
        //执行更新
        AssertUtil.isTrue(customerReprieveMapper.updateByPrimaryKeySelective(customerReprieve)<1,"更新失败！");


    }

    /**
     * 删除暂缓数据
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCustomerRepr(Integer id) {
        //判断id是否空
        AssertUtil.isTrue(null==id,"记录不存在！");
        //查询数据
        CustomerReprieve customerReprieve = customerReprieveMapper.selectByPrimaryKey(id);
        AssertUtil.isTrue(null==customerReprieve,"记录不存在！");
        //设置值
        customerReprieve.setIsValid(0);
        customerReprieve.setUpdateDate(new Date());
        //执行更新
        AssertUtil.isTrue(customerReprieveMapper.updateByPrimaryKeySelective(customerReprieve)<1,"删除失败！");
    }
}
