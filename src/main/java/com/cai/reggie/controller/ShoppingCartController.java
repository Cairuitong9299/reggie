package com.cai.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;

import com.cai.reggie.common.BaseContext;
import com.cai.reggie.common.R;
import com.cai.reggie.entity.ShoppingCart;
import com.cai.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 查看购物车功能
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> query(){
        LambdaQueryWrapper<ShoppingCart> shoppingCartLambdaQueryWrapper = new LambdaQueryWrapper<>();
        shoppingCartLambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartLambdaQueryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = shoppingCartService.list(shoppingCartLambdaQueryWrapper);
        return R.success(list);
    }

    /**
     * 清空购物车
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> delete(){
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        shoppingCartService.remove(lambdaQueryWrapper);
        return R.success("删除成功");
    }

    /**
     * 减少菜品数量
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> sub(@RequestBody ShoppingCart shoppingCart){
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        if (dishId != null){
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        }else {
            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(lambdaQueryWrapper);
        Integer number = cartServiceOne.getNumber();
        if (number > 1 ){
            cartServiceOne.setNumber(number-1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            shoppingCartService.removeById(cartServiceOne);
        }
        return R.success(cartServiceOne);
    }

    /**
     * 添加菜品到购物车功能
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> add(@RequestBody ShoppingCart shoppingCart){
        log.info("购物车数据：{}",shoppingCart);
        //设置用户ID
        shoppingCart.setUserId(BaseContext.getCurrentId());
        //判断数据是否存在购物车里，是则加一，否则添加至购物车
        Long dishId = shoppingCart.getDishId();
        LambdaQueryWrapper<ShoppingCart> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(ShoppingCart::getUserId,BaseContext.getCurrentId());
        if (dishId != null){
            //如果为空则 添加到购物车的是套餐
            lambdaQueryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //如果不等于空 表示添加到购物车的是菜品

            lambdaQueryWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }
        ShoppingCart cartServiceOne = shoppingCartService.getOne(lambdaQueryWrapper);
        if (cartServiceOne != null){
            //表示该数据在购物车里已经有一份了则直接给数量加1就好
            Integer number = cartServiceOne.getNumber();
            cartServiceOne.setNumber(number + 1);
            shoppingCartService.updateById(cartServiceOne);
        }else {
            //表示购物车里没有这个数据 则把数据添加到购物车里
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            shoppingCartService.save(shoppingCart);
            cartServiceOne = shoppingCart;
        }
        return R.success(cartServiceOne);
    }
}
