package com.cai.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cai.reggie.dto.DishDto;
import com.cai.reggie.entity.Dish;
import com.cai.reggie.entity.DishFlavor;

public interface DishService extends IService<Dish> {
    public void savaWithFlavor(DishDto dishDto);

    //根据id来查询菜品信息和口味信息
    public DishDto getByIdWithFlavor(Long id);

    //更新菜品信息和口味信息
    public void updateWithFlavor(DishDto dishDto);
}
