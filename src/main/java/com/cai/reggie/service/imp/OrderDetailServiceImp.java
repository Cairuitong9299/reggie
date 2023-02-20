package com.cai.reggie.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cai.reggie.entity.OrderDetail;
import com.cai.reggie.mapper.OrderDetailMapper;
import com.cai.reggie.service.OrderDetailService;
import org.springframework.stereotype.Service;

@Service
public class OrderDetailServiceImp extends ServiceImpl <OrderDetailMapper, OrderDetail> implements OrderDetailService {
}
