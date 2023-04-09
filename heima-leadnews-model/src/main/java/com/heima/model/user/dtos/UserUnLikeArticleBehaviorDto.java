package com.heima.model.user.dtos;

import lombok.Data;

/**
 * @author: xiaocai
 * @since: 2023/04/08/16:25
 */
@Data
public class UserUnLikeArticleBehaviorDto {

    private Long articleId;
//    0 不喜欢      1 取消不喜欢
    private Short type;


}
