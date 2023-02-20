package com.cai.reggie.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cai.reggie.common.CustomException;
import com.cai.reggie.common.R;
import com.cai.reggie.dto.SetmealDto;
import com.cai.reggie.entity.Setmeal;
import com.cai.reggie.entity.SetmealDish;
import com.cai.reggie.mapper.SetmealMapper;
import com.cai.reggie.service.SetmealDishService;
import com.cai.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SetmealServiceImp extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {
    @Autowired
    SetmealDishService setmealDishService;

    /**
     * 新填套餐同时保存套餐和菜品的关联关系
     *
     * @param setmealDto
     */
    @Override
    @Transactional
    public void saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息,操作setmeal,执行insert操作
        this.save(setmealDto);
        //保存套餐和菜品的关联关系，操作SetmealDish表,执行insert操作
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes.stream().map((item) -> {
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 删除套餐，同时删除套餐的关联数据
     * @param ids
     */
    @Override
    @Transactional
    public void removeWithDish(List<Long> ids) {
        //判断套餐状态，确定是否可以删除
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(Setmeal::getId,ids);
        lambdaQueryWrapper.eq(Setmeal::getStatus,1);
        int count = this.count(lambdaQueryWrapper);
        if (count > 0){
            throw  new CustomException("删除的商品，有正在售卖中的");
        }

        //如果可以，删除套餐表中的数据
        this.removeByIds(ids);

        //如果可以，删除关系表中的关系
        LambdaQueryWrapper<SetmealDish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        dishLambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(dishLambdaQueryWrapper);

    }

    @GetMapping("/{id}")
    public R<String> SetmealUpdate(@RequestBody SetmealDto setmealDto){

        return null;
    }

}
