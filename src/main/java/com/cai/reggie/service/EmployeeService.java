package com.cai.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cai.reggie.entity.Employee;

public interface EmployeeService extends IService<Employee> {
    abstract String decode(String password);
    abstract Employee inquire(String name);
}
