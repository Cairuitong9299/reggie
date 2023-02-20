package com.cai.reggie.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cai.reggie.common.CustomException;
import com.cai.reggie.entity.Category;
import com.cai.reggie.entity.Dish;
import com.cai.reggie.entity.Setmeal;
import com.cai.reggie.mapper.CategoryMapper;
import com.cai.reggie.service.CategoryService;
import com.cai.reggie.service.DishService;
import com.cai.reggie.service.SetmealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CategoryServiceImp extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private DishService dishService;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private CategoryService categoryService;


    @Override
    public void remove(Long ids) {
        //查询当前的分类是否关联了菜品，是则抛出异常
        LambdaQueryWrapper<Dish> dishLambdaQueryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件，根据分类id进行查询
        dishLambdaQueryWrapper.eq(Dish::getCategoryId, ids);
        int count = dishService.count(dishLambdaQueryWrapper);
        if (count > 0) {
            //如果大于0，则抛出业务异常
            throw  new CustomException("当前分类下关联了菜品，不允许删除");
        }

        //查询当前的分类是否关联了套餐，是则抛出异常
        LambdaQueryWrapper<Setmeal> setmealLambdaQueryWrapper = new LambdaQueryWrapper<>();
        setmealLambdaQueryWrapper.eq(Setmeal::getCategoryId, ids);
        int count1 = setmealService.count(setmealLambdaQueryWrapper);
        if (count1 > 0){
            ////如果大于0，则抛出业务异常
            throw  new CustomException("当前分类下关联了套餐，不允许删除");
        }

        //都都没有则正常删除
        categoryService.removeById(ids);
    }
}
