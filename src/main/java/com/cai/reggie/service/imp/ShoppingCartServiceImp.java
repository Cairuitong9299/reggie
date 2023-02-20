package com.cai.reggie.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cai.reggie.entity.ShoppingCart;
import com.cai.reggie.mapper.ShoppingCartMapper;
import com.cai.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;

@Service
public class ShoppingCartServiceImp extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {
}
