package com.msb.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.msb.crm.base.BaseService;
import com.msb.crm.dao.CustomerLossMapper;
import com.msb.crm.dao.CustomerMapper;
import com.msb.crm.dao.CustomerOrderMapper;
import com.msb.crm.query.CustomerQuery;
import com.msb.crm.query.SaleChanceQuery;
import com.msb.crm.utils.AssertUtil;
import com.msb.crm.utils.PhoneUtil;
import com.msb.crm.vo.Customer;
import com.msb.crm.vo.CustomerLoss;
import com.msb.crm.vo.CustomerOrder;
import com.msb.crm.vo.SaleChance;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@Service
public class CustomerService extends BaseService<Customer,Integer> {
    @Resource
    private CustomerMapper customerMapper;
    @Resource
    private CustomerLossMapper customerLossMapper;
    @Resource
    private CustomerOrderMapper customerOrderMapper;

    /**
     * 查询客户
     * @param
     * @return
     */
    public Map<String, Object> queryCustomerByParams(CustomerQuery customerQuery) {
        //开启分页
        Map<String, Object> map = new HashMap<String, Object>();
        //得到分页对象
        PageHelper.startPage(customerQuery.getPage(), customerQuery.getLimit());
        PageInfo<Customer> pageInfo = new PageInfo<>(customerMapper.selectByParams(customerQuery));
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        //存入分页数据
        map.put("data", pageInfo.getList());
        return map;
    }

    /**
     * 添加客户
     * @param customer
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addCustomer(Customer customer){
        //参数校验
        checkCustomerParams(customer.getName(),customer.getFr(),customer.getPhone());
        //名称校验
        Customer temp=customerMapper.queryCustomerByName(customer.getName());
        //判断是否存在
        AssertUtil.isTrue(null!=temp,"客户名已存在！");
        //设置默认值
        customer.setIsValid(1);
        customer.setCreateDate(new Date());
        customer.setUpdateDate(new Date());
        customer.setState(0);
        //客户编号
        String hkno="KH"+System.currentTimeMillis();
        customer.setKhno(hkno);
        //添加
        AssertUtil.isTrue(customerMapper.insertSelective(customer)<1,"添加失败！");

    }

    /**
     * 更新用户
     * @param customer
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomer(Customer customer){
        //参数校验
        AssertUtil.isTrue(null==customer.getId(),"记录不存在！");
        //查询客户是否存在
        Customer temp = customerMapper.selectByPrimaryKey(customer.getId());
        //参数校验
        checkCustomerParams(customer.getName(),customer.getFr(),customer.getPhone());
        //客户重名。排除自己
        AssertUtil.isTrue(null!=temp && !(temp.getId().equals(customer.getId())),"名称存在！");
        //设置默认值
        customer.setUpdateDate(new Date());
        //添加
        AssertUtil.isTrue(customerMapper.updateByPrimaryKeySelective(customer)<1,"更新失败！");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteCustomer(Integer id){
        //参数校验
        AssertUtil.isTrue(null==id || null==customerMapper.selectByPrimaryKey(id),"记录不存在！");
        //设置默认值
        Customer customer = customerMapper.selectByPrimaryKey(id);
        customer.setUpdateDate(new Date());
        //设置状态
        customer.setIsValid(0);

        //执行操作
        AssertUtil.isTrue(customerMapper.updateByPrimaryKeySelective(customer)<1,"删除失败！");
    }
    /**
     * 参数校验
     * @param name 名字
     * @param fr 法人
     * @param phone 手机号码
     */
    private void checkCustomerParams(String name, String fr, String phone) {
        //名字非空
        AssertUtil.isTrue(StringUtils.isBlank(name),"名字不能为空！");
        //法人非空
        AssertUtil.isTrue(StringUtils.isBlank(fr),"法人不能为空！");
        //手机号码非空
        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号码不能为空！");
        //手机号码格式正确
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"手机号码格式不对！");
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public void updateCustomerState(){
        //查询待流失的客户数据
        List<Customer> lossCustomerList=customerMapper.queryLossCustomers();
        //将流失客户批量添加到客户流失表中
        //判断是否存在
        if (lossCustomerList!=null && lossCustomerList.size()>0){
            //定义集合，接收流失客户的id
            List<Integer> lossCustomerId=new ArrayList<>();
            //定义列表
            List<CustomerLoss> customerLossList=new ArrayList<>();
            //遍历查询到的客户流失的数据
            lossCustomerList.forEach(customer -> {
                CustomerLoss customerLoss=new CustomerLoss();
                //时间
                customerLoss.setCreateDate(new Date());
                //经理
                customerLoss.setCusManager(customer.getCusManager());
                //名称
                customerLoss.setCusName(customer.getName());
                //编号
                customerLoss.setCusNo(customer.getKhno());
                //是否有效
                customerLoss.setIsValid(1);
                //修改时间
                customerLoss.setUpdateDate(new Date());
                //流失状态 0暂缓 1流失
                customerLoss.setState(0);
                //最后下单时间
                //通过id查询最后一单的记录
                CustomerOrder customerOrder=customerOrderMapper.queryLossCustomerOrderByCustomerId(customer.getId());
                //判断是否有，存在则设置最后下单时间
                if (customerOrder!=null){
                    customerLoss.setLastOrderTime(customerOrder.getOrderDate());
                }
                //设置到集合中
                customerLossList.add(customerLoss);
                //id设置到集合中
                lossCustomerId.add(customer.getId());
            });
            AssertUtil.isTrue(customerLossMapper.insertBatch(customerLossList)!=customerLossList.size(),"设置流失客户失败！");
            //批量更新客户的流失状态
            AssertUtil.isTrue(customerMapper.updateCustomerStateByIds(lossCustomerId)!=lossCustomerId.size(),"设置流失客户失败");
        }
    }

    /**
     * 根据参数查询客户贡献信息
     * @param customerQuery 包含分页信息和查询条件的客户查询对象
     * @return 返回一个包含查询结果和分页信息的Map对象
     */
    public Map<String,Object> queryCustomerContributionByParams(CustomerQuery customerQuery){
        // 初始化返回的map对象，用于存储查询结果和分页信息
        Map<String, Object> map = new HashMap<String, Object>();

        // 开启分页插件
        PageHelper.startPage(customerQuery.getPage(), customerQuery.getLimit());

        // 执行查询，获得分页信息对象
        PageInfo<Map<String,Object>> pageInfo = new PageInfo<>(customerMapper.queryCustomerContributionByParams(customerQuery));

        // 在map中存储查询状态码、消息、总记录数和查询结果数据
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());

        // 存入分页数据
        map.put("data", pageInfo.getList());

        return map;
    }


    /**
     * 客户构成，折线图处理
     * @return
     */
    public Map<String,Object> countCustomerMake(){
        Map<String,Object> map=new HashMap<>();
        //查询客户构成数据的列表
        List<Map<String, Object>> maps = customerMapper.countCustomerMake();
        //折线图X轴数据
        List<String> data1=new ArrayList<>();
        //折线图Y轴数据
        List<Integer> data2=new ArrayList<>();
        //判断列表 循环设置数据
        if (maps != null && maps.size()>0){
            //遍历集合
            maps.forEach(stringObjectMap -> {
                data1.add(stringObjectMap.get("level").toString());
                data2.add(Integer.parseInt(stringObjectMap.get("total").toString()));
            });
        }
        //将xy轴数据设置到集合中
        map.put("data1",data1);
        map.put("data2",data2);

        return map;
    }

    /**
     * 饼状图处理
     * @return
     */
    public Map<String, Object> countCustomerMake02() {
        Map<String,Object> map=new HashMap<>();
        //查询客户构成数据的列表
        List<Map<String, Object>> maps = customerMapper.countCustomerMake();
        //饼状图数据 数组（数组中是字符串）
        List<String> data1=new ArrayList<>();
        //折线图Y轴数据
        List<Map<String,Object>> data2=new ArrayList<>();
        //判断列表 循环设置数据
        if (maps != null && maps.size()>0){
            //遍历集合
            maps.forEach(m -> {
                //饼状图数据 数组
                data1.add(m.get("level").toString());
                //饼状图数据 数组
                Map<String,Object> dataMap=new HashMap<>();
                dataMap.put("name",m.get("level"));
                dataMap.put("value",m.get("total"));
                data2.add(dataMap);
            });
        }
        //将xy轴数据设置到集合中
        map.put("data1",data1);
        map.put("data2",data2);

        return map;
    }
}
