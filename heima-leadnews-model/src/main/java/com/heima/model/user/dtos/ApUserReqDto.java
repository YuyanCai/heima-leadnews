package com.heima.model.user.dtos;

import lombok.Data;

/**
 * @author: xiaocai
 * @since: 2023/04/03/20:23
 */
@Data
public class ApUserReqDto {
    private String id;
    private String msg;
    private String page;
    private String size;
    private String status;
}
