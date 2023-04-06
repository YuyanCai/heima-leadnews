package com.heima.model.wemedia.dtos;

import lombok.Data;

/**
 * @author: xiaocai
 * @since: 2023/02/16/16:06
 */
@Data
public class AdminNewsPageReqDto {

    private Integer id;
    private String msg;
    private Integer page;
    private Integer size;
    private Integer status;
    private String title;

}
