package com.cai.reggie.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cai.reggie.entity.AddressBook;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AddressBookMapper extends BaseMapper<AddressBook> {
}
