package com.cai.reggie.dto;

import com.cai.reggie.entity.User;
import lombok.Data;

import java.util.List;


/**
 * 用户信息
 */
@Data
public class UserDto extends User {
    private List<User> user;

    private String code;
}
