package com.cai.reggie.dto;


import com.cai.reggie.entity.OrderDetail;
import com.cai.reggie.entity.Orders;
import lombok.Data;
import java.util.List;

@Data
public class OrdersDto extends Orders {

    private String userName;

    private String phone;

    private String address;

    private String consignee;
    private int sumNum;

    private List<OrderDetail> orderDetails;
	
}
