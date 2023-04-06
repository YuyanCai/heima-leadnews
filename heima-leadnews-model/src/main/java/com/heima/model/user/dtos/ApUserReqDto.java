package com.heima.model.user.dtos;

import lombok.Data;

/**
 * @author: xiaocai
 * @since: 2023/04/03/20:23
 */
@Data
public class ApUserReqDto {
    private Integer id;
    private String msg;
    private Integer page;
    private Integer size;
    private Integer status;
}
