package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import io.swagger.models.auth.In;
import lombok.Data;

/**
 * @author: xiaocai
 * @since: 2023/04/04/22:48
 */
@Data
public class WmUserPageReqDto extends PageRequestDto {
    private Integer id;
    private Integer page;
    private Integer size;
    private Integer status;
    private String  msg;
}
