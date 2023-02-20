package com.cai.reggie.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cai.reggie.dto.DishDto;
import com.cai.reggie.entity.Dish;
import com.cai.reggie.entity.DishFlavor;
import com.cai.reggie.mapper.DishMapper;
import com.cai.reggie.service.DishFlavorService;
import com.cai.reggie.service.DishService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DishMapperImp extends ServiceImpl<DishMapper, Dish> implements DishService {
    @Autowired
    private DishFlavorService dishFlavorService;
    /**
     * 新添菜品，同时保存口味数据
     * @param dishDto
     */
    @Transactional
    public void savaWithFlavor(DishDto dishDto) {
        this.save(dishDto);
        //保存菜品口味到菜品口味表
        Long dishId = dishDto.getId();
        List<DishFlavor> flavor = dishDto.getFlavors();
        flavor = flavor.stream().map((item)->{
            item.setDishId(dishId);
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavor);
    }

    /**
     * 根据id来查询菜品信息和口味信息
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        //查询菜品基本信息
        Dish dish = this.getById(id);
        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);
        //根据id来查询口味信息,从口味表来查询
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dish.getId());
        List<DishFlavor> flavors = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(flavors);
        return dishDto;
    }

    @Override
    @Transactional
    public void updateWithFlavor(DishDto dishDto) {
        //更新dish表
        this.updateById(dishDto);
        //清理当前dishFlavor表
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper();
        LambdaQueryWrapper<DishFlavor> queryWrapper = lambdaQueryWrapper.eq(DishFlavor::getDishId, dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加当前提交过来的口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream().map((item)->{
            item.setDishId(dishDto.getId());
            return item;
        }).collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);
    }
}
