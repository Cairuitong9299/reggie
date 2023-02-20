package com.cai.reggie.service.imp;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cai.reggie.entity.Employee;
import com.cai.reggie.mapper.EmployeeMapper;
import com.cai.reggie.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

@Service
public class EmployeeServiceImp extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Autowired
    private EmployeeService employeeService;

    //加密
    @Override
    public  String decode(String password) {
        String depassword = DigestUtils.md5DigestAsHex(password.getBytes());
        return depassword;
    }


    //根据用户名查询数据库
    @Override
    public Employee inquire(String name) {
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,name);
        Employee emp = employeeService.getOne(queryWrapper);
        return  emp;
    }


}
