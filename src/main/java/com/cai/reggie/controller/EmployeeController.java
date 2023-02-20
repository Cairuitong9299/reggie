package com.cai.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cai.reggie.common.R;
import com.cai.reggie.entity.Employee;
import com.cai.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee) {
        String password = employee.getPassword();
        String username = employee.getUsername();
        password = employeeService.decode(password);
        Employee emp = employeeService.inquire(username);

        //判断员工是否存在
        if (emp == null) {
            return R.error("登录失败，账号不存在");
        }

        //判断密码是否正确
        if (!emp.getPassword().equals(password)) {
            return R.error("登录失败，密码错误");
        }

        //判断员工状态是否被封禁
        if (emp.getStatus() == 0) {
            return R.error("账号禁用");
        }

        request.getSession().setAttribute("employee", emp.getId());
        return R.success(emp);
    }


    /**
     * 员工退出
     *
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request) {
        //清除Session中保存的当前员工数据
        request.getSession().removeAttribute("employee");
        return R.success("账号成功登出");
    }

    /**
     * 新增员工
     *
     * @param employee
     * @return
     */
    @PostMapping()
    public R<String> save(HttpServletRequest request, @RequestBody Employee employee) {
        log.info("新增员工，员工信息：{}", employee.toString());
        //设置初始密码，需要进行md5处理
        employee.setPassword(DigestUtils.md5DigestAsHex("123456".getBytes()));
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
        //获取当前登录用户的ID
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    /**
     * 员工信息分页查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> page(int page, int pageSize, String name) {
        log.info("page={}, pageSiz={}, name={}", page, pageSize, name);
        //构造分页构造器
        Page pageInfo = new Page(page,pageSize);
        //构造条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper();
        //添加过滤条件
        //第一个参数判断是否查询，第二个是数据库中的字段名，第三个是该字段的值。
        queryWrapper.like(StringUtils.isNotEmpty(name),Employee::getName,name);
        //添加排序条件
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        //执行查询
        employeeService.page(pageInfo,queryWrapper);

        return R.success(pageInfo);
    }

    /**
     * 根据
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> update(HttpServletRequest request,@RequestBody Employee employee){
//        Long empID = (Long) request.getSession().getAttribute("employee");
        //employee.setUpdateTime(LocalDateTime.now());
        //employee.setUpdateUser(empID);
        long id = Thread.currentThread().getId();
        log.info("线程的id为：{}",id);
        employeeService.updateById(employee);
        return R.success("员工信息修改成功");
    }

    /**
     * 根据id来查询员工信息
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getById(@PathVariable Long id){
        log.info("根据id查询员工信息");
        Employee employee = employeeService.getById(id);
        if (employee != null){
            return R.success(employee);
        }
        return R.error("查询不到该员工");
    }
}