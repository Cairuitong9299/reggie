package com.cai.reggie.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cai.reggie.common.BaseContext;
import com.cai.reggie.common.CustomException;
import com.cai.reggie.entity.*;
import com.cai.reggie.mapper.OrderMapper;
import com.cai.reggie.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

@Service
public class OrderServiceImp extends ServiceImpl<OrderMapper, Orders> implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private UserService userService;
    @Autowired
    private AddressBookService addressBookService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @Override
    @Transactional
    public void submit(Orders orders) {
        long id = IdWorker.getId();
        //获取当前用户id
        Long currentId = BaseContext.getCurrentId();
        //查询当前用户的购物车数据
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,currentId);
        List<ShoppingCart> shoppingCarts = shoppingCartService.list(queryWrapper);
        //查询用户数据
        User user = userService.getById(currentId);
        //查询用户地址
        AddressBook addressBook = addressBookService.getById(orders.getAddressBookId());
        if (addressBook == null){
            throw new CustomException("地址不能为空，请输入");
        }
        //遍历购物车数据
        AtomicInteger amount = new AtomicInteger(0);
        List<OrderDetail> orderDetails = shoppingCarts.stream().map((item) -> {
            OrderDetail orderDetail = new OrderDetail();
            orderDetail.setOrderId(id);
            orderDetail.setNumber(item.getNumber());
            orderDetail.setDishFlavor(item.getDishFlavor());
            orderDetail.setDishId(item.getDishId());
            orderDetail.setSetmealId(item.getSetmealId());
            orderDetail.setName(item.getName());
            orderDetail.setImage(item.getImage());
            orderDetail.setAmount(item.getAmount());
            amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
            return orderDetail;
        }).collect(Collectors.toList());
        //向订单表插入数据
        orders.setNumber(String.valueOf(id));
        orders.setStatus(2);
        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setAmount(new BigDecimal(amount.get()));
        orders.setUserName(user.getName());
        orders.setPhone(user.getPhone());
        orders.setUserId(currentId);
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        orders.setConsignee(addressBook.getConsignee());
        this.save(orders);
        //向订单明细中插入多条数据
        orderDetailService.saveBatch(orderDetails);
        //清空购物车
        shoppingCartService.remove(queryWrapper);

    }
}
