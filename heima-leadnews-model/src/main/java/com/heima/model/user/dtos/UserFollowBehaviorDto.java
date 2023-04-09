package com.heima.model.user.dtos;

import lombok.Data;

/**
 * @author: xiaocai
 * @since: 2023/04/08/16:25
 */
@Data
public class UserFollowBehaviorDto {

    private Long articleId;
    //作者
    private Integer authorId;
//    0  关注   1  取消
    private Short operation;

}
