package com.cai.reggie.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cai.reggie.entity.SetmealDish;
import com.cai.reggie.mapper.SetmealDishMapper;
import com.cai.reggie.service.SetmealDishService;
import org.springframework.stereotype.Service;

@Service
public class SetmealDishServiceImp extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {
}
