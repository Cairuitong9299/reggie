package com.cai.reggie.dto;


import com.cai.reggie.entity.Setmeal;
import com.cai.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
