package com.msb.crm;

import com.alibaba.fastjson.JSON;
import com.msb.crm.base.ResultInfo;
import com.msb.crm.exceptions.AuthException;
import com.msb.crm.exceptions.NoLoginException;
import com.msb.crm.exceptions.ParamsException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

@Component
public class          GlobalExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        /**
         * 解决异常的方法。
         * 根据异常类型和处理器类型，决定返回的视图或者JSON数据。
         * 如果处理器方法上标注了@ResponseBody，则返回JSON数据，否则返回视图。
         * 对于不同的异常类型，会添加不同的错误信息到返回的数据中。
         *
         * @param request HttpServletRequest对象，代表客户端的请求。
         * @param response HttpServletResponse对象，用于向客户端发送响应。
         * @param handler 处理请求的处理器对象，用于判断异常发生时的处理逻辑。
         * @param ex 发生的异常对象。
         * @return 根据异常情况返回ModelAndView对象，用于导向错误页面或返回JSON数据。
         */                                                                                              
        ModelAndView mv=new ModelAndView();
        mv.setViewName("error");
        mv.addObject("code",400);
        mv.addObject("msg","系统异常，请稍后再试...");

        // 如果异常为NoLoginException，则重定向到登录页面
        if(ex instanceof NoLoginException){
            ModelAndView modelAndView=new ModelAndView("redirect:/index");
            return modelAndView;
        }

        // 判断处理器是否为HandlerMethod类型，用于确定异常处理的方式
        if(handler instanceof  HandlerMethod){
            HandlerMethod hm = (HandlerMethod) handler;
            ResponseBody responseBody= hm.getMethod().getDeclaredAnnotation(ResponseBody.class);

            // 如果处理器方法上标注了@ResponseBody，则返回JSON数据
            if(null == responseBody){
                // 处理器方法响应内容为视图
                if(ex instanceof ParamsException){
                    ParamsException pe = (ParamsException) ex;
                    mv.addObject("msg",pe.getMsg());
                    mv.addObject("code",pe.getCode());
                }else if(ex instanceof AuthException){// 认证异常
                    AuthException ae = (AuthException) ex;
                    mv.addObject("msg",ae.getMsg());
                    mv.addObject("code",ae.getCode());
                }
                return mv;
            }else{
                // 处理器方法响应内容为JSON
                ResultInfo resultInfo=new ResultInfo();
                resultInfo.setCode(300);
                resultInfo.setMsg("系统错误,请稍后再试...");
                if(ex instanceof  ParamsException){
                    ParamsException pe = (ParamsException) ex;
                    resultInfo.setCode(pe.getCode());
                    resultInfo.setMsg(pe.getMsg());
                }else if(ex instanceof  AuthException) { // 认证异常
                    AuthException ae = (AuthException) ex;
                    resultInfo.setCode(ae.getCode());
                    resultInfo.setMsg(ae.getMsg());
                }
                // 设置响应类型为JSON，并将结果写入响应体中
                response.setContentType("application/json;charset=utf-8");
                response.setCharacterEncoding("utf-8");
                PrintWriter pw=null;
                try {
                    pw=response.getWriter();
                    pw.write(JSON.toJSONString(resultInfo));
                    pw.flush();
                } catch (Exception e) {
                    e.printStackTrace();
                }finally {
                    if(null !=pw){
                        pw.close();
                    }
                }
                return null;
            }
        }else{
            return mv;
        }

    }

}
