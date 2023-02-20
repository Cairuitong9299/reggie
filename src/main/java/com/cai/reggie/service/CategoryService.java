package com.cai.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cai.reggie.entity.Category;

public interface CategoryService extends IService<Category> {
    void remove(Long ids);
}
