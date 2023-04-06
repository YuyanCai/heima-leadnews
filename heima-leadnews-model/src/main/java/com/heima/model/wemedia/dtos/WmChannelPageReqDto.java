package com.heima.model.wemedia.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import lombok.Data;

/**
 * @author: xiaocai
 * @since: 2023/04/03/20:05
 */
@Data
public class WmChannelPageReqDto extends PageRequestDto {


    private String name;
    /**
     * 页数
     */
    private Integer page;
    /**
     * 每页大小
     */
    private Integer size;
}
