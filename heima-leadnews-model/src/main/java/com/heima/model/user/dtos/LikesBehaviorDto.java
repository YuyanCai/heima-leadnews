package com.heima.model.user.dtos;

import lombok.Data;

/**
 * @author: xiaocai
 * @since: 2023/04/08/16:25
 */
@Data
public class LikesBehaviorDto {

    private Long articleId;
    //0 点赞   1 取消点赞
    private Short operation;
    //0文章  1动态   2评论
    private Short type;

}
