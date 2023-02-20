package com.cai.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.cai.reggie.Utils.SMSUtils;
import com.cai.reggie.Utils.ValidateCodeUtils;
import com.cai.reggie.common.R;
import com.cai.reggie.dto.UserDto;
import com.cai.reggie.entity.User;
import com.cai.reggie.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.concurrent.TimeUnit;

@RestController
@Slf4j
@RequestMapping("/user")
public class UserController {
    @Autowired
    private UserService userService;
    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 发送手机验证码
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(@RequestBody User user, HttpSession session){
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotEmpty(phone)) {
            //生成随机的四位数验证码
//           String code = ValidateCodeUtils.generateValidateCode(4).toString();
            String code = "9299";
            //调用阿里云的短信服务API来发送短信
//           SMSUtils.sendMessage("蔡氏程序","SMS_269570627",phone,code);

            //把验证码储存起来
//            session.setAttribute(phone,code);
            //将验证码缓存到redis数据库中去，并且设置有效期为5分钟
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);

            return R.success("手机验证码短信发送成功");

        }

        return R.success("手机验证码短信发送失败");

    };

    /**
     * 移动端用户登录
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody UserDto user,HttpSession httpSession ){
        log.info("接收到的Phone为：{}，code为：{}",user.getPhone(),user.getCode());
        //获取手机号和验证码
        String phone = user.getPhone().toString();
        String code = user.getCode().toString();

        //从redis中获取缓存的验证码
        Object sessionCode = redisTemplate.opsForValue().get(phone);
        //校验验证码，从session中获取保存的验证码和提交的验证码进行比对,如果比对成功则给予登录
        if (sessionCode != null && sessionCode.equals(code)){
            //判断当前手机号 是否在用户表里。
            LambdaQueryWrapper<User> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(User::getPhone,phone);
            User user1 = userService.getOne(lambdaQueryWrapper);
            if (user1 == null){
                //如果是新用户则自动完成注册
                user1 = new User();
                user1.setPhone(phone);
                user1.setStatus(1);
                userService.save(user1);
            }
            //如果登录成功则删除redis中缓存的对应数据
            redisTemplate.delete(phone);
            httpSession.setAttribute("user",user1.getId());
            return R.success(user1);
        }
        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> loginout(HttpSession session){
//        BaseContext.setCurrentId(null);
        session.removeAttribute("user");
        return R.success("成功退出");
    }

}
