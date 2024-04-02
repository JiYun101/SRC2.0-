package com.msb.crm.interceptor;

import com.msb.crm.dao.UserMapper;
import com.msb.crm.exceptions.NoLoginException;
import com.msb.crm.utils.LoginUserUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.logging.Handler;

public class NoLoginInterceptor extends HandlerInterceptorAdapter {
    @Resource
    UserMapper userMapper;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //获取cookie中的用户id
        Integer userId= LoginUserUtil.releaseUserIdFromCookie(request);
        //判断id是否为空，且数据库中村长该id的用户记录
        if (null==userId || userMapper.selectByPrimaryKey(userId)==null){
            //抛出未登录异常
            throw new NoLoginException();
        }
        return true;
    }
}
