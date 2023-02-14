package com.heima.entity;

import lombok.Data;

import java.util.Date;

/**
 * @author: xiaocai
 * @since: 2023/02/13/21:37
 */

@Data
public class Student {
    private String name;//姓名
    private int age;//年龄
    private Date birthday;//生日
    private Float money;//钱包
}