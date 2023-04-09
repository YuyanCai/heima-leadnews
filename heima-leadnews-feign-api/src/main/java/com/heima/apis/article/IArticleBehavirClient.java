package com.heima.apis.article;

import com.heima.apis.article.fallback.IArticleClientFallback;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.LikesBehaviorDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: xiaocai
 * @since: 2023/02/16/16:03
 */
@FeignClient(value = "leadnews-article")
@Component
public interface IArticleBehavirClient {

    @PostMapping("/api/v1/article/update")
    public ResponseResult updateArticle(@RequestBody LikesBehaviorDto dto);


}
