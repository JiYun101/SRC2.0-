package com.msb.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.msb.crm.base.BaseService;
import com.msb.crm.dao.CusDevPlanMapper;
import com.msb.crm.dao.SaleChanceMapper;
import com.msb.crm.query.CusDevPlanQuery;
import com.msb.crm.query.SaleChanceQuery;
import com.msb.crm.utils.AssertUtil;
import com.msb.crm.vo.CusDevPlan;
import com.msb.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class CusDevPlanService extends BaseService<CusDevPlan,Integer> {

    @Resource
    CusDevPlanMapper cusDevPlanMapper;
    @Resource
    SaleChanceMapper saleChanceMapper;

    /**
     * 多条件分页查询客户开发计划
     * @param cusDevPlanQuery 查询条件对象，包含页码和每页数量等
     * @return 包含查询结果和总数等信息的Map对象
     */
    public Map<String, Object> queryCusDevPlanByParams(CusDevPlanQuery cusDevPlanQuery) {
        Map<String, Object> resultMap = new HashMap<>();

        // 检查页码和每页数量的合法性
        if (cusDevPlanQuery.getPage() <= 0 || cusDevPlanQuery.getLimit() <= 0) {
            throw new IllegalArgumentException("页码和每页数量必须为正数");
        }

        try {
            // 开启分页
            PageHelper.startPage(cusDevPlanQuery.getPage(), cusDevPlanQuery.getLimit());
            PageInfo<CusDevPlan> pageInfo = new PageInfo<>(cusDevPlanMapper.selectByParams(cusDevPlanQuery));

            resultMap.put("code", 0);
            resultMap.put("msg", "");
            resultMap.put("count", pageInfo.getTotal());
            resultMap.put("data", pageInfo.getList());
        } catch (Exception e) {
            // 异常处理，可根据实际情况记录日志或抛出更具体的异常
            e.printStackTrace();
            resultMap.put("code", -1); // 或者其他表示异常的代码
            resultMap.put("msg", "查询客户开发计划时发生异常");
        }

        return resultMap;
    }

    /**
     * 添加营销计划
     * @param cusDevPlan
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCusDevPlan(CusDevPlan cusDevPlan){
        //校验参数
        checkCusPlanParams(cusDevPlan);
        //设置默认值
        cusDevPlan.setIsValid(1);
        //设置默认时间
        cusDevPlan.setCreateDate(new Date());
        //设置修改时间
        cusDevPlan.setUpdateDate(new Date());
        //执行添加
        AssertUtil.isTrue(cusDevPlanMapper.insertSelective(cusDevPlan)!=1,"添加失败！");
    }

    /**
     *更新计划项
     * @param
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCusDevPlan(CusDevPlan cusDevPlan){
        //校验参数
        //计划id非空
        AssertUtil.isTrue(null==cusDevPlan.getId()
                ||cusDevPlanMapper.selectByPrimaryKey(cusDevPlan.getId())==null,"数据异常，请重试！");
        checkCusPlanParams(cusDevPlan);
        //设置默认
        cusDevPlan.setUpdateDate(new Date());
        //执行更新
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan)<1,"更新失败！");
    }
    /**
     * 检查内容
     * @param cusDevPlan
     */
    private void checkCusPlanParams(CusDevPlan cusDevPlan) {
        Integer sId=cusDevPlan.getSaleChanceId();
        //机会id非空
        AssertUtil.isTrue(null==sId||saleChanceMapper.selectByPrimaryKey(sId)==null,"数据异常！请重试！");
        //计划项内容非空
        AssertUtil.isTrue(StringUtils.isBlank(cusDevPlan.getPlanItem()),"计划项内容不能为空！");
        //时间非空
        AssertUtil.isTrue(null==cusDevPlan.getPlanDate(),"时间不能为空！");
    }

    /**
     * 删除计划项
     * @param id
     */
    public void deleteCusDevPlan(Integer id) {
        //判断id是否为空
        AssertUtil.isTrue(null==id,"数据不存在！");
        //查询计划项
        CusDevPlan cusDevPlan = (CusDevPlan) cusDevPlanMapper.selectByPrimaryKey(id);
        //设置记录无效
        cusDevPlan.setIsValid(0);
        cusDevPlan.setUpdateDate(new Date());
        AssertUtil.isTrue(cusDevPlanMapper.updateByPrimaryKeySelective(cusDevPlan)!=1,"删除失败！");

    }
}
