package com.cai.reggie.service.imp;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cai.reggie.entity.AddressBook;
import com.cai.reggie.mapper.AddressBookMapper;
import com.cai.reggie.service.AddressBookService;
import org.springframework.stereotype.Service;

@Service
public class AddressBookServiceImp extends ServiceImpl<AddressBookMapper, AddressBook>  implements AddressBookService {
}
