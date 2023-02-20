package com.cai.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cai.reggie.common.BaseContext;
import com.cai.reggie.common.R;
import com.cai.reggie.dto.OrdersDto;
import com.cai.reggie.entity.OrderDetail;
import com.cai.reggie.entity.Orders;
import com.cai.reggie.service.OrderDetailService;
import com.cai.reggie.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/order")
@Slf4j
public class OrdersController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        log.info("订单数据:{}",orders);
        orderService.submit(orders);
        return R.success("下单成功");
    }

    @GetMapping("/userPage")
    public R<Page> page(int page, int pageSize){
        //分页
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        Page<OrdersDto> ordersDtoPage = new Page<>();
        //条件查询器
        LambdaQueryWrapper<Orders> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(BaseContext.getCurrentId()!=null,Orders::getUserId, BaseContext.getCurrentId());
        wrapper.orderByDesc(Orders::getOrderTime);
        //根据分页和条件查询取来查询
        orderService.page(ordersPage, wrapper);
        //拷贝
        BeanUtils.copyProperties(ordersPage,ordersDtoPage,"records");
        List<Orders> records = ordersPage.getRecords();
        List<OrdersDto> collect = records.stream().map((item) -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            Long id = item.getId();
            Orders orders = orderService.getById(id);
            String ddh = orders.getNumber();
            LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
            lambdaQueryWrapper.eq(OrderDetail::getOrderId, ddh);
            List<OrderDetail> list = orderDetailService.list(lambdaQueryWrapper);
            int i = 0;
            for (OrderDetail list1 : list) {
                i = i + list1.getNumber().intValue();
            }
            ordersDto.setSumNum(i);
            return ordersDto;
        }).collect(Collectors.toList());

        ordersDtoPage.setRecords(collect);

        return R.success(ordersDtoPage);
    }

    @PostMapping("/again")
    public R<String> again(@RequestBody Orders orders){
        Orders orders1 = orderService.getById(orders);
        String number = orders1.getNumber();
        Long Id = IdWorker.getId();
        orders1.setId(Id);
        orders1.setNumber(String.valueOf(IdWorker.getId()));
        orders1.setStatus(2);
        orders1.setOrderTime(LocalDateTime.now());
        orders1.setCheckoutTime(LocalDateTime.now());
        orderService.save(orders1);
        //更新order_detail表
        LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(OrderDetail::getOrderId,number);
        List<OrderDetail> list = orderDetailService.list(lambdaQueryWrapper);
        List<OrderDetail> orderDetails = list.stream().map((item) -> {
            Long oid = IdWorker.getId();
            item.setId(oid);
            Long i = Long.parseLong(orders1.getNumber());
            item.setOrderId(i);
            return item;
        }).collect(Collectors.toList());
        orderDetailService.saveBatch(orderDetails);
        return R.success("再来一单");
    }

    @GetMapping("/page")
    public R<Page> page(Long page , Long pageSize , Long number , String beginTime , String endTime){
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        Page<OrdersDto> orderDetailPage = new Page<>();
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.like(number!=null,Orders::getNumber,number);
        if (beginTime!=null && endTime!=null){
            ordersLambdaQueryWrapper.ge(Orders::getOrderTime,beginTime);
            ordersLambdaQueryWrapper.le(Orders::getOrderTime,endTime);
        }
        ordersLambdaQueryWrapper.orderByDesc(Orders::getOrderTime);
        orderService.page(ordersPage,ordersLambdaQueryWrapper);
        BeanUtils.copyProperties(ordersPage,orderDetailPage,"records");
        List<Orders> records = ordersPage.getRecords();
        List<OrdersDto> collect = records.stream().map((item -> {
            OrdersDto ordersDto = new OrdersDto();
            BeanUtils.copyProperties(item, ordersDto);
            String userName = "用户:" + item.getUserId();
            ordersDto.setUserName(userName);
            return ordersDto;
        })).collect(Collectors.toList());
        orderDetailPage.setRecords(collect);
        return R.success(orderDetailPage);
    }

    @PutMapping
    public R<String> delivery(@RequestBody Orders orders){
        LambdaQueryWrapper<Orders> ordersLambdaQueryWrapper = new LambdaQueryWrapper<>();
        ordersLambdaQueryWrapper.eq(Orders::getId,orders.getId());
        Orders orders1 = orderService.getOne(ordersLambdaQueryWrapper);
        orders1.setStatus(orders.getStatus());
        orderService.updateById(orders1);
        return R.success("操作成功");
    }

}
