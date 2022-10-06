/**
 * Create by xy
 * 2022-05-12 20:48
 */

package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController { //
    @Autowired
    private UserService userService;

    /**
     * 手机短信验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpServletRequest request){
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)){
            //生成随机的4位验证码
            String code = ValidateCodeUtils.generateValidateCode(4).toString();//数字写几就随机生成几位验证码
            log.info("code={}",code);

            //调用阿里云提供的短信服务API发送短信（真实场景就会收到短信，这里先不管）
//            SMSUtils.sendMessage("瑞吉外卖","",phone,code);//申请好的签名，模板code,手机号，验证码


            //需要将生成的验证码保存起来，可以保存到session中
            request.getSession().setAttribute(phone,code);

            return R.success("手机验证码短信发送成功");
        }


        return R.error("短信发送失败");
    }

    /**
     * 移动端用户登录
     * @param map
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpServletRequest request){//user没有code属性，需要解决,可以用map对应phone,code
        log.info(map.toString());
        //获取手机号
        String phone = map.get("phone").toString();

        //获取前端输入的验证码
        String code = map.get("code").toString();

        //从session中获得保存的验证码，
        Object codeInSession = request.getSession().getAttribute(phone);

        //进行比对,页面提交的验证码和session保存的验证码进行比对
        if (codeInSession!=null&&codeInSession.equals(code)){
            //如果比对成功，说明登录成功

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = userService.getOne(queryWrapper);

            if (user==null){
                //判断手机号是否为新用户，如果新用户就会自动注册（这里用户是无感知的）
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);//不设置也可以 （使用，禁用）
                userService.save(user);
            }
            request.getSession().setAttribute("user",user.getId()); //这里别忘了，过滤器需要获得用户id
            return R.success(user);

        }
        return R.error("短信发送失败");
    }
}
