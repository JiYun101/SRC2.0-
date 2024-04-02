package com.msb.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.msb.crm.base.BaseService;
import com.msb.crm.dao.SaleChanceMapper;
import com.msb.crm.enums.DevResult;
import com.msb.crm.enums.StateStatus;
import com.msb.crm.query.SaleChanceQuery;
import com.msb.crm.utils.AssertUtil;
import com.msb.crm.utils.PhoneUtil;
import com.msb.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class SaleChanceService extends BaseService<SaleChance, Integer> {
    @Resource
    private SaleChanceMapper saleChanceMapper;

    public Map<String, Object> querySaleChancesByParams(SaleChanceQuery saleChanceQuery) {
        //开启分页
        Map<String, Object> map = new HashMap<String, Object>();
        //得到分页对象
        PageHelper.startPage(saleChanceQuery.getPage(), saleChanceQuery.getLimit());
        PageInfo<SaleChance> pageInfo = new PageInfo<SaleChance>(saleChanceMapper.selectByParams(saleChanceQuery));
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        //存入分页数据
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 添加销售机会
     *
     * @param saleChance 销售机会对象，包含客户名称、联系人、联系电话等信息
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addSaleChance(SaleChance saleChance) {
        /* 参数校验 */
        checkSaleChanceParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());

        /* 设置相关字段默认值 */
        // 设置是否有效字段为有效（1）
        saleChance.setIsValid(1);
        // 设置创建时间和修改时间，默认为当前系统时间
        saleChance.setCreateDate(new Date());
        saleChance.setUpdateDate(new Date());

        // 判断是否设置了指派人
        if (StringUtils.isBlank(saleChance.getAssignMan())) {
            // 未设置指派人时，设置状态为未分配，开发状态为未开发
            saleChance.setState(StateStatus.UNSTATE.getType());
            saleChance.setAssignTime(null);
            saleChance.setDevResult(DevResult.UNDEV.getStatus());
        } else {
            // 设置了指派人时，设置状态为已分配，开发状态为开发中，并设置指派时间和当前系统时间
            saleChance.setState(StateStatus.STATED.getType());
            saleChance.setAssignTime(new Date());
            saleChance.setDevResult(DevResult.DEVING.getStatus());
        }

        // 执行插入操作，并校验是否添加成功
        AssertUtil.isTrue(saleChanceMapper.insertSelective(saleChance) != 1, "添加失败！");
    }


    /**
     * 更新记录
     *
     * @param saleChance
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChance(SaleChance saleChance) {
        //参数校验
        AssertUtil.isTrue(null == saleChance.getId(), "待更新记录不存在！");
        //通过主键查询对象
        SaleChance temp = saleChanceMapper.selectByPrimaryKey(saleChance.getId());
        //判断数据库是否有记录
        AssertUtil.isTrue(temp == null, "待更新记录不存在！");
        //参数校验
        checkSaleChanceParams(saleChance.getCustomerName(), saleChance.getLinkMan(), saleChance.getLinkPhone());

        //设置相关参数的默认值
        //更新时间默认时间
        saleChance.setUpdateDate(new Date());
        //指派人，判断原始是否存在
        if (StringUtils.isBlank(temp.getAssignMan())) {//不存在
            //判断修改后是否存在
            if (!StringUtils.isBlank(saleChance.getAssignMan())) {//前空后有
                //时间
                saleChance.setAssignTime(new Date());
                //状态
                saleChance.setState(StateStatus.STATED.getType());
                saleChance.setDevResult(DevResult.DEVING.getStatus());
            }
        } else {
            if (StringUtils.isBlank(saleChance.getAssignMan())){//前有后无
                //时间
                saleChance.setAssignTime(null);
                //状态
                saleChance.setState(StateStatus.UNSTATE.getType());
                saleChance.setDevResult(DevResult.UNDEV.getStatus());
            }else {//前有后有
                //判断是否同一个
                if (!saleChance.getAssignMan().equals(temp.getAssignMan())){
                    //更新时间
                    saleChance.setAssignTime(new Date());
                }else {
                    saleChance.setAssignTime(temp.getAssignTime());
                }
            }
        }
        //更新，返回行数
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)!=1,"更新失败！");
    }

    /**
     * 删除营销计划
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteSaleChance(Integer[] ids){
        //判断id是否为空
        AssertUtil.isTrue(null==ids || ids.length<1,"待删除记录不存在！");
        //执行删除操作，实际上就是更新状态
        AssertUtil.isTrue(saleChanceMapper.deleteBatch(ids)!=ids.length,"删除失败！");
    }

    private void checkSaleChanceParams(String customerName, String linkMan, String linkPhone) {
        //客户名称非空判断
        AssertUtil.isTrue(StringUtils.isBlank(customerName), "客户名称不能为空！");
        //联系人非空校验
        AssertUtil.isTrue(StringUtils.isBlank(linkMan), "联系人不能为空！");
        //联系号码非空
        AssertUtil.isTrue(StringUtils.isBlank(linkPhone), "联系号码不能为空！");
        //手机号码校验
        AssertUtil.isTrue(!PhoneUtil.isMobile(linkPhone), "联系号码格式不正确！");
    }

    /**
     * 更新营销计划开发状态
     * @param id
     * @param devResult
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateSaleChanceDevResult(Integer id, Integer devResult) {
        //判断id是否为空
        AssertUtil.isTrue(null==id,"待删除记录不存在！");
        //通过id 查询记录
        SaleChance saleChance = selectByPrimaryKey(id);
        //判断对象是否为空
        AssertUtil.isTrue(null==saleChance,"记录不存在！");
        //设置开发状态
        saleChance.setDevResult(devResult);
        AssertUtil.isTrue(saleChanceMapper.updateByPrimaryKeySelective(saleChance)!=1,"更新失败！");

    }
}
