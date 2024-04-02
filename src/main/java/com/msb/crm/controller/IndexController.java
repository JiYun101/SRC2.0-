package com.msb.crm.controller;

import com.msb.crm.base.BaseController;
import com.msb.crm.base.ResultInfo;
import com.msb.crm.exceptions.ParamsException;
import com.msb.crm.service.PermissionService;
import com.msb.crm.service.UserService;
import com.msb.crm.utils.LoginUserUtil;
import com.msb.crm.vo.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
public class IndexController extends BaseController {
    @Autowired
    private UserService userService;

    @Resource
    private PermissionService permissionService;
    /**
     * 系统登录页
     * @return
     */
    @RequestMapping("index")
    public String index(){
        return "index";
    }

    // 系统界面欢迎页
    @RequestMapping("welcome")
    public String welcome(){
        return "welcome";
    }
    /**
     * 后端管理主页面
     * @return
     */
    @RequestMapping("main")
    public String main(HttpServletRequest request){
        //从cookie中获取用户id
        Integer userId = LoginUserUtil.releaseUserIdFromCookie(request);
        //查询用户对象
        User user=userService.selectByPrimaryKey(userId);
        request.getSession().setAttribute("user",user);

        List<String> permissions= permissionService.queryUserHasRoleHasPermissionByUserId(userId);
        request.getSession().setAttribute("permissions",permissions);
        return "main";

    }



    /*@PostMapping("user/updatePwd")
    @ResponseBody
    public ResultInfo updateUserPassword(HttpServletRequest request,String oldPassword,String newPassword,String repeatPassword){
        ResultInfo resultInfo=new ResultInfo();
        try {
            //获取userid
            Integer userId=LoginUserUtil.releaseUserIdFromCookie(request);
            //调用方法修改密码
            userService.updatePassword(userId,oldPassword,newPassword,repeatPassword);
        }catch (ParamsException p){
            resultInfo.setCode(p.getCode());
            resultInfo.setMsg(p.getMsg());
            p.printStackTrace();
        }catch (Exception e){
            resultInfo.setCode(500);
            resultInfo.setMsg("修改失败！");
            e.printStackTrace();
        }
        return resultInfo;
    }*/
}
