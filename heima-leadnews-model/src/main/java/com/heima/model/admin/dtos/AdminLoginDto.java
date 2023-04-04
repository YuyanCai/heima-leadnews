package com.heima.model.admin.dtos;

import lombok.Data;

/**
 * @author: xiaocai
 * @since: 2023/04/03/18:51
 */

@Data
public class AdminLoginDto {

    /**
     * 用户名
     */
    private String name;
    /**
     * 密码
     */
    private String password;
}