package com.msb.crm.aspect;

import com.msb.crm.annoation.RequiredPermission;
import com.msb.crm.exceptions.AuthException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;
import java.util.List;

@Component
@Aspect
public class PermissionProxy {

    @Resource
    private HttpSession httpSession;

    @Around(value = "@annotation(com.msb.crm.annoation.RequiredPermission)")
    public Object around(ProceedingJoinPoint pjp) throws Throwable {
        Object result=null;
        //得到当前用户拥有的权限
        List<String> permissions= (List<String>) httpSession.getAttribute("permissions");
        //判断是否有权限
        if (null==permissions||permissions.size()<1){
            //抛出异常
            throw new AuthException();
        }
        //得到对应的目标
        MethodSignature methodSignature= (MethodSignature) pjp.getSignature();
        //得到方法上的注解
        RequiredPermission requiredPermission=methodSignature.getMethod().getDeclaredAnnotation(RequiredPermission.class);
        //判断状态码
        if (!(permissions.contains(requiredPermission.code()))){
            throw new AuthException();
        }
        result=pjp.proceed();
        return result;
    }
}
