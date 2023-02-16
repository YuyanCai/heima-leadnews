package com.heima.article.service;

import com.heima.model.article.pojos.ApArticle;

/**
 * @author: xiaocai
 * @since: 2023/02/16/21:43
 */
public interface ArticleFreemarkerService {

    /**
     * 生成静态文件上传到minIO中
     * @param apArticle
     * @param content
     */
    public void buildArticleToMinIO(ApArticle apArticle, String content);
}
