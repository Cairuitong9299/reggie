package com.cai.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cai.reggie.common.R;
import com.cai.reggie.dto.SetmealDto;
import com.cai.reggie.entity.Setmeal;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface SetmealService extends IService<Setmeal> {
    public void saveWithDish(SetmealDto setmealDto);
    public void removeWithDish(List<Long> ids);

}
