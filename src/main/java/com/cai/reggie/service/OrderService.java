package com.cai.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cai.reggie.entity.Orders;

public interface OrderService extends IService<Orders> {
    public void submit(Orders orders);
}
