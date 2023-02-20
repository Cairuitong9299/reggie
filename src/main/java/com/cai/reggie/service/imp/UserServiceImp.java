package com.cai.reggie.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cai.reggie.entity.User;
import com.cai.reggie.mapper.UserMapper;
import com.cai.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImp extends ServiceImpl<UserMapper, User> implements UserService {
}
