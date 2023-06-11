package com.heima.article.mess;

import lombok.Data;

/**
 * @author: xiaocai
 * @since: 2023/06/11/15:07
 */
@Data
public class ArticleVisitStreamMess {
    /**
     * 文章id
     */
    private Long articleId;
    /**
     * 阅读
     */
    private int view;
    /**
     * 收藏
     */
    private int collect;
    /**
     * 评论
     */
    private int comment;
    /**
     * 点赞
     */
    private int like;
}
