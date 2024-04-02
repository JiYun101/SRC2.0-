package com.msb.crm.service;

import com.msb.crm.base.BaseService;
import com.msb.crm.dao.UserMapper;
import com.msb.crm.dao.UserRoleMapper;
import com.msb.crm.model.UserModel;
import com.msb.crm.utils.AssertUtil;
import com.msb.crm.utils.Md5Util;
import com.msb.crm.utils.PhoneUtil;
import com.msb.crm.utils.UserIDBase64;
import com.msb.crm.vo.User;
import com.msb.crm.vo.UserRole;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class UserService extends BaseService<User,Integer> {
    @Resource
    private UserMapper userMapper;
    @Resource
    private UserRoleMapper userRoleMapper;

    /**
     * 登陆
     * @param userName
     * @param userPwd
     * @return
     */
    public UserModel userLogin(String userName,String userPwd){
        //1.参数判断，判断用户姓名、用户密码非空
        checkLoginParams(userName,userPwd);

        //2.调用数据访问层，通过用户名查询用户记录，返回用户对象
        User user =userMapper.queryUserByUserName(userName);
        
        //3.判断用户对象是否为空
        AssertUtil.isTrue(user==null,"用户姓名不存在！");
        
        //4.判断密码是否正确，比较客户端传递的数据密码与数据库中查询的用户对象中的用户密码
        checkUserPwd(userPwd,user.getUserPwd());
        //返回构建用户对象
        return buildUserInfo(user);
    }

    /**
     * 更新密码
     * @param userId
     * @param oldPwd
     * @param newPwd
     * @param repeatPwd
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updatePassword(Integer userId,String oldPwd,String newPwd,String repeatPwd){
        //通过用户id查询记录，返回用户对象
        User user=userMapper.selectByPrimaryKey(userId);
        //判断用户是否存在
        AssertUtil.isTrue(null==user,"待更新记录不存在！");
        //参数校验
        checkPasswordParams(user,oldPwd,newPwd,repeatPwd);
        //设置新密码
        user.setUserPwd(Md5Util.encode(newPwd));
        //判断受影响的行数
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)<1,"修改失败！");
    }

    /**
     * 校验密码
     * @param user
     * @param oldPwd
     * @param newPwd
     * @param repeatPwd
     */
    private void checkPasswordParams(User user, String oldPwd, String newPwd, String repeatPwd) {
        //判断原始密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(oldPwd),"原始密码不能为空！");
        //判断旧密码是否正确
        AssertUtil.isTrue(!user.getUserPwd().equals(Md5Util.encode(oldPwd)),"旧密码不正确！");
        //判断新密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(newPwd),"新密码不能为空！");
        //判断是否与旧密码一致
        AssertUtil.isTrue(oldPwd.equals(newPwd),"新密码与旧密码一致！");
        //判断确认密码是否为空
        AssertUtil.isTrue(StringUtils.isBlank(repeatPwd),"请再次输入密码！");
        //判断密码是否一致
        AssertUtil.isTrue(!newPwd.equals(repeatPwd),"两次密码不一致！");
    }

    private UserModel buildUserInfo(User user){
            UserModel userModel=new UserModel();
            //userModel.setUserId(user.getId());
            userModel.setUserIdStr(UserIDBase64.encoderUserID(user.getId()));
            userModel.setUserName(user.getUserName());
            userModel.setTrueName(user.getTrueName());
            return userModel;
        }

    /**
     * 检查密码
     * @param userPwd
     * @param Pwd
     */
    private void checkUserPwd(String userPwd, String Pwd) {
        //将客户端传递的密码加密
        userPwd = Md5Util.encode(userPwd);
        //判断密码是否相等
        AssertUtil.isTrue(!userPwd.equals(Pwd),"用户密码不正确！");

    }

    /**
     * 检查输入状态
     * @param userName
     * @param userPwd
     */
    private void checkLoginParams(String userName,String userPwd){
        //验证用户姓名
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户姓名不能为空");
        //验证用户密码
        AssertUtil.isTrue(StringUtils.isBlank(userPwd),"用户密码不能为空");
    }

    /**
     * 查询用户
     * @return
     */
    public List<Map<String,Object>> queryAllSales(){
        return userMapper.queryAllSales();
    }

    /**
     * 添加用户
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void addUser(User user){
        //参数校验
        checkUserParams(user.getUserName(),user.getEmail(),user.getPhone(),null);
        //设置默认值
        user.setIsValid(1);
        user.setCreateDate(new Date());
        user.setUpdateDate(new Date());
        //设置默认密码
        user.setUserPwd(Md5Util.encode("123456"));
        //判断
        AssertUtil.isTrue(userMapper.insertSelective(user)<1,"添加失败！");

        //用户角色关联
        relationUserRole(user.getId(),user.getRoleIds());
    }

    /**
     * 用户角色关联
     * @param
     * @param roleIds
     */
    private void relationUserRole(Integer userId, String roleIds) {
        //通过用户id查询角色记录
        Integer count=userRoleMapper.countUserRoleByUserId(userId);
        //判断角色记录是否存在
        if (count>0){
            AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"角色添加失败！");
        }
        //判断id是否存在
        if (StringUtils.isNotBlank(roleIds)){
            //设置用户数据到集合中
            List<UserRole> userRoleList=new ArrayList<>();
            //把id转换成数组
            String[] roleIdsArray=roleIds.split(",");
            //遍历数组，得到对应的用户角色，并设置到集合中
            for (String roleId:roleIdsArray){
                UserRole userRole=new UserRole();
                userRole.setRoleId(Integer.parseInt(roleId));
                userRole.setUserId(userId);
                userRole.setCreateDate(new Date());
                userRole.setUpdateDate(new Date());
                //设置到集合中
                userRoleList.add(userRole);
            }
            //批量添加用户角色记录
            AssertUtil.isTrue(userRoleMapper.insertBatch(userRoleList)!=userRoleList.size(),"添加失败！");
        }
    }

    /**
     * 更新用户
     * @param user
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void updateUser(User user){
        //判断用户是否存在 判断id
        AssertUtil.isTrue(null==user.getId(),"更新记录不存在！");
        //通过id查询
        User temp = userMapper.selectByPrimaryKey(user.getId());
        //判断是存在
        AssertUtil.isTrue(null==temp,"更新记录不存在！");
        //参数校验
        checkUserParams(user.getUserName(),user.getEmail(),user.getPhone(),user.getId());
        //设置默认
        user.setUpdateDate(new Date());
        AssertUtil.isTrue(userMapper.updateByPrimaryKeySelective(user)!=1,"更新失败！");

        //用户角色关联
        relationUserRole(user.getId(),user.getRoleIds());
    }

    /**
     * 用户创建参数校验
     * @param userName
     * @param email
     * @param phone
     */
    private void checkUserParams(String userName, String email, String phone,Integer userId) {
        //判断用户名是否为空
        AssertUtil.isTrue(StringUtils.isBlank(userName),"用户名不能为空！");
        //判断用户名是否唯一
        //通过名字查询用户
        //如果用户名存在，且与当前的修改记录不是同一个，则表示其他用户占用了用户名，不可以修改
        User temp=userMapper.queryUserByUserName(userName);
        //如果查出来了，就表示有该用户
        AssertUtil.isTrue(null!=temp && !(temp.getId().equals(userId)),"用户名存在，请重新输入！");
        //邮箱非空
        AssertUtil.isTrue(StringUtils.isBlank(email),"邮箱不能为空！");
        //手机非空
        AssertUtil.isTrue(StringUtils.isBlank(phone),"手机号不能为空！");
        //格式判断
        AssertUtil.isTrue(!PhoneUtil.isMobile(phone),"手机号格式不正确！");
    }

    /**
     * 删除用户
     * @param ids
     */
    @Transactional(propagation = Propagation.REQUIRED)
    public void deleteByIds(Integer[] ids) {
        //判断id是否存在
        AssertUtil.isTrue(null==ids || ids.length==0,"待删除记录不存在");
        //执行删除操作
        AssertUtil.isTrue(userMapper.deleteBatch(ids)!=ids.length,"删除失败！");
        //遍历用户id的数组
        for (Integer userId:ids){
            //通过用户id查询对应的用户角色记录
            Integer count=userRoleMapper.countUserRoleByUserId(userId);
            //判断用户角色记录是否存在
            if (count>0){
                AssertUtil.isTrue(userRoleMapper.deleteUserRoleByUserId(userId)!=count,"删除失败！");
            }
        }
    }

    /**
     * 查询所有客户经理
     * @return
     */
    public List<Map<String, Object>> queryAllCustomerManager() {
        return userMapper.queryAllCustomerManager();
    }
}
