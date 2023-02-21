package com.cai.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cai.reggie.common.R;
import com.cai.reggie.dto.SetmealDto;
import com.cai.reggie.entity.Category;
import com.cai.reggie.entity.Setmeal;
import com.cai.reggie.entity.SetmealDish;
import com.cai.reggie.service.CategoryService;
import com.cai.reggie.service.SetmealDishService;
import com.cai.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 套餐管理
 */
@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishService setmealDishService;
    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @CacheEvict(value = "setmealCache",allEntries = true)
    @PostMapping
    public R<String> save(@RequestBody SetmealDto setmealDto) {
        setmealService.saveWithDish(setmealDto);
        return R.success("新增套餐成功");
    }

    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        Page<Setmeal> pageInfo = new Page<>(page, pageSize);
        Page<SetmealDto> dtoPage = new Page<>(page, pageSize);
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.like(StringUtils.isNotEmpty(name), Setmeal::getName, name);
        lambdaQueryWrapper.orderByAsc(Setmeal::getUpdateTime).orderByAsc(Setmeal::getPrice);
        setmealService.page(pageInfo, lambdaQueryWrapper);
        BeanUtils.copyProperties(pageInfo, dtoPage, "records");
        List<Setmeal> records = pageInfo.getRecords();
        List<SetmealDto> list = records.stream().map((item)->{
            SetmealDto setmealDto = new SetmealDto();
            BeanUtils.copyProperties(item,setmealDto);
            //分类ID
            Long categoryId = item.getCategoryId();
            //根据分类id来查询分类对象
            Category category = categoryService.getById(categoryId);
            if (category!=null){
                //分类名称
                String categoryName = category.getName();
                setmealDto.setCategoryName(categoryName);
            }
            return setmealDto;
        }).collect(Collectors.toList());
        dtoPage.setRecords(list);
        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @DeleteMapping
    @CacheEvict(value = "setmealCache",allEntries = true)
    public R<String> delete(@RequestParam List<Long> ids){
        setmealService.removeWithDish(ids);
        return R.success("套餐删除成功");
    }

    /**
     * 停售操作
     */
    @PostMapping("/status/{status}")
    public R<String> pause(@PathVariable int status,@RequestParam List<Long> ids){
        for (Long id:ids){
            Setmeal setmeal = setmealService.getById(id);
            setmeal.setStatus(status);
            setmealService.updateById(setmeal);
        }
        return R.success("操作成功");
    }

    @GetMapping("/{id}")
    public R<SetmealDto> setmealEq(@PathVariable Long id){
        Setmeal setmeal = setmealService.getById(id);
        SetmealDto setmealDto = new SetmealDto();
        BeanUtils.copyProperties(setmeal,setmealDto);
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,id);
        List<SetmealDish> list = setmealDishService.list(lambdaQueryWrapper);
        setmealDto.setSetmealDishes(list);
        return R.success(setmealDto);
    }

    @PutMapping
    public R<String> setmealUpdate(@RequestBody SetmealDto setmealDto){
        setmealService.updateById(setmealDto);
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(SetmealDish::getSetmealId,setmealDto.getId());
        setmealDishService.remove(lambdaQueryWrapper);
        List<SetmealDish> list = setmealDto.getSetmealDishes();
        list = list.stream().map((item)->{
            item.setSetmealId(setmealDto.getId());
            return item;
        }).collect(Collectors.toList());
        setmealDishService.saveBatch(list);
        return R.success("修改成功");
    }

    @Cacheable(value = "setmealCache" ,key = "#p0 + '_'+ #p1")
    @GetMapping("/list")
    public R<List<Setmeal>> query(@RequestParam Long categoryId,@RequestParam String status){
        LambdaQueryWrapper<Setmeal> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(categoryId!=null,Setmeal::getCategoryId,categoryId);
        lambdaQueryWrapper.eq(status !=null,Setmeal::getStatus,status);
        lambdaQueryWrapper.orderByAsc(Setmeal::getUpdateTime);
        List<Setmeal> list = setmealService.list(lambdaQueryWrapper);
        return R.success(list);
    }
}
