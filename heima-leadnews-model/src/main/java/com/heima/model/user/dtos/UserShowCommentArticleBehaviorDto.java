package com.heima.model.user.dtos;

import lombok.Data;

/**
 * @author: xiaocai
 * @since: 2023/04/08/16:25
 */
@Data
public class UserShowCommentArticleBehaviorDto {

    private Long articleId;

    private Integer index;
    private Integer minDate;


}
