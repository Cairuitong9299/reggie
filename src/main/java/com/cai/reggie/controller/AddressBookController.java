package com.cai.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.cai.reggie.common.BaseContext;
import com.cai.reggie.common.R;
import com.cai.reggie.entity.AddressBook;
import com.cai.reggie.service.AddressBookService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.List;

@RequestMapping("/addressBook")
@RestController
@Slf4j
public class AddressBookController {
    @Autowired
    private AddressBookService addressBookService;

    /**
     * 新添地址
     *
     * @param addressBook
     * @return
     */
    @PostMapping
    public R<String> save(@RequestBody AddressBook addressBook) {
        addressBook.setUserId(BaseContext.getCurrentId());
        addressBookService.save(addressBook);
        return R.success("添加成功");
    }

    @GetMapping("/list")
    public R<List<AddressBook>> query(AddressBook addressBook){
        addressBook.setUserId(BaseContext.getCurrentId());
        LambdaQueryWrapper<AddressBook> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(addressBook.getUserId()!=null,AddressBook::getUserId,addressBook.getUserId());
        lambdaQueryWrapper.orderByDesc(AddressBook::getIsDefault);
        lambdaQueryWrapper.orderByAsc(AddressBook::getUpdateTime);
        List<AddressBook> list = addressBookService.list(lambdaQueryWrapper);
        return R.success(list);
    }

    @GetMapping("/{id}")
    public R<AddressBook> query(@PathVariable Long id){
        log.info("id={}",id);
        AddressBook addressBook = addressBookService.getById(id);
        if (addressBook != null){
            return  R.success(addressBook);
        }else {
            return R.error("没有找到该地址信息");
        }

    }

    @PutMapping
    public R<String> update(@RequestBody AddressBook addressBook){
        if (addressBook!=null){
            addressBookService.updateById(addressBook);
            return R.success("更新成功");
        }
        return R.error("更新失败");
    }

    @PutMapping("/default")
    public R<AddressBook> defaultAddressBook(@RequestBody AddressBook addressBook){
        LambdaUpdateWrapper<AddressBook> wrapper = new LambdaUpdateWrapper<>();
        wrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        wrapper.set(AddressBook::getIsDefault,0);
        addressBookService.update(wrapper);
        addressBook.setIsDefault(1);
        addressBookService.updateById(addressBook);
        return R.success(addressBook);
    }

    @GetMapping("/default")
    public R<AddressBook> getdefaultAddressBook(){
        LambdaQueryWrapper<AddressBook> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(AddressBook::getUserId,BaseContext.getCurrentId());
        wrapper.eq(AddressBook::getIsDefault,1);
        return R.success(addressBookService.getOne(wrapper));
    }

}
