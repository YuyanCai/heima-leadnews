package com.heima.article.feign;

import com.heima.apis.article.IArticleBehavirClient;
import com.heima.apis.article.IArticleClient;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LikesBehaviorDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: xiaocai
 * @since: 2023/02/16/16:07
 */
@RestController
public class ArticleBehavirClient implements IArticleBehavirClient {
    @Autowired
    private ApArticleService apArticleService;

    @Autowired
    private ApArticleMapper apArticleMapper;


    @Override
    @PostMapping("/api/v1/article/update")
    @Transactional
    public ResponseResult updateArticle(LikesBehaviorDto dto) {

//        1.获得文章信息
        Long articleId = dto.getArticleId();
        ApArticle apArticle = apArticleMapper.selectById(articleId);
        Integer likes = apArticle.getLikes();
//        2.判断是点赞还是取消点赞
        Short operation = dto.getOperation();
        if (operation == 0) {
            likes = likes + 1;
        } else {
            likes = likes - 1;
        }

        int update = apArticleMapper.updateById(apArticle);

        if (update < 1) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }


}
