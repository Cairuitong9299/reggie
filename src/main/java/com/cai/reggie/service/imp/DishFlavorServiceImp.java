package com.cai.reggie.service.imp;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cai.reggie.entity.DishFlavor;
import com.cai.reggie.mapper.DishFlavorMapper;
import com.cai.reggie.service.DishFlavorService;
import org.springframework.stereotype.Service;

@Service
public class DishFlavorServiceImp extends ServiceImpl<DishFlavorMapper, DishFlavor> implements DishFlavorService {

}