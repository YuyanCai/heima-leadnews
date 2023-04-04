package com.heima.model.wemedia.dtos;

import lombok.Data;

import java.util.Date;

/**
 * @author: xiaocai
 * @since: 2023/04/03/20:05
 */
@Data
public class WmSensitiveDto {

    /**
     * 创建时间
     */
    private Date createdTime;

    /**
     * id
     */
    private Integer id;

    /**
     * 敏感词
     */
    private String sensitives;
}
