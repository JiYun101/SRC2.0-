package com.msb.crm.service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.msb.crm.base.BaseService;
import com.msb.crm.dao.OrderDetailsMapper;
import com.msb.crm.query.OrderDetailsQuery;
import com.msb.crm.vo.Customer;
import com.msb.crm.vo.OrderDetails;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class OrderDetailsService extends BaseService<OrderDetails,Integer> {
    @Resource
    private OrderDetailsMapper orderDetailsMapper;

    /**
     * 根据参数查询订单详情信息。
     *
     * @param orderDetailsQuery 包含查询条件和分页信息的订单详情查询对象。
     * @return 返回一个包含查询结果和分页信息的Map对象。
     *         其中，"code"表示状态码（0表示成功）；
     *         "msg"表示消息或异常信息；
     *         "count"表示总记录数；
     *         "data"表示查询结果列表。
     */
    public Map<String, Object> queryOrderDetailsByParams(OrderDetailsQuery orderDetailsQuery) {
        //开启分页
        Map<String, Object> map = new HashMap<String, Object>();
        //得到分页对象
        PageHelper.startPage(orderDetailsQuery.getPage(), orderDetailsQuery.getLimit());
        PageInfo<OrderDetails> pageInfo = new PageInfo<>(orderDetailsMapper.selectByParams(orderDetailsQuery));
        map.put("code", 0);
        map.put("msg", "");
        map.put("count", pageInfo.getTotal());
        //存入分页数据
        map.put("data", pageInfo.getList());
        return map;
    }
}
