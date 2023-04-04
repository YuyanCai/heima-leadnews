package com.heima.model.wemedia.dtos;

import io.swagger.models.auth.In;
import lombok.Data;

import java.util.Date;

/**
 * @author: xiaocai
 * @since: 2023/04/03/20:05
 */
@Data
public class WmChannelDto {


    private String createdTime;

    private Integer id;

    private String sensitives;
    private String description;
    private boolean isDefault;
    private String name;
    private Integer ord;
    private boolean status;
}
