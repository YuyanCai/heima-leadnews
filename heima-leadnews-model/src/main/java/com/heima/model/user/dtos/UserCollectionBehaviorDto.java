package com.heima.model.user.dtos;

import lombok.Data;

import java.util.Date;

/**
 * @author: xiaocai
 * @since: 2023/04/08/16:25
 */
@Data
public class UserCollectionBehaviorDto {

//    文章id
    private Long entryId;
//   0收藏    1取消收藏
    private Short operation;

    private Date publishedTime;
//0文章    1动态
    private Short type;

}
