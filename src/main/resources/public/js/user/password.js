layui.use(['form','jquery','jquery_cookie'], function () {
    var form = layui.form,
        layer = layui.layer,
        $ = layui.jquery,
        $ = layui.jquery_cookie($);
    /**
     * 表单监听
     */
    form.on('submit(saveBtn)',function (data) {
        console.log(data.field);
        $.ajax({
            type:"post",
            url:ctx+"/user/updatePwd",
            data:{
                oldPassword:data.field.old_password,
                newPassword:data.field.new_password,
                repeatPassword:data.field.again_password
            },
            success:function (result){
                //判断是否成功
                if(result.code==200){
                    layer.msg("用户密码修改成功，请重新登陆...",function (){
                        $.removeCookie("userIdStr",{domain:"localhost",path:"/crm"});
                        $.removeCookie("userName",{domain:"localhost",path:"/crm"});
                        $.removeCookie("tureName",{domain:"localhost",path:"/crm"});
                        window.parent.location.href=ctx+"/index";
                    });
                }else {
                    layer.msg(result.msg,{icon:5});
                }
            }
        })
    });

});