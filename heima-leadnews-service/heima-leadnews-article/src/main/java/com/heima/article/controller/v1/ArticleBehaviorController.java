package com.heima.article.controller.v1;

import com.heima.article.entity.ApCollection;
import com.heima.article.mapper.ApCollectionDao;
import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.LoadArticleBehaviorDto;
import com.heima.model.user.dtos.UserCollectionBehaviorDto;
import com.heima.model.user.dtos.UserReadArticleBehaviorDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.AppThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: xiaocai
 * @since: 2023/04/08/20:31
 */
@RestController
@RequestMapping("/api/v1/")
public class ArticleBehaviorController {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ApCollectionDao apCollectionDao;

    //收藏和取消收藏
    @PostMapping("collection_behavior")
    @Transactional
    public ResponseResult collectionBehavior(@RequestBody UserCollectionBehaviorDto dto) {
        String redis_key = "heima:collection_behavior:";

        //1.获取用户id和文章id
        String entryId = "";
        ApUser user = AppThreadLocalUtil.getUser();
        String userId = user.getId() + "";
        if (!StringUtils.isEmpty(dto.getEntryId())) {
            entryId = dto.getEntryId() + "";
        }

        if (dto.getOperation() == 0) {
            cacheService.sAdd(redis_key + entryId, userId);
            ApCollection apCollection = new ApCollection();
            apCollection.setArticleId(dto.getEntryId());
            apCollection.setType(0);
            apCollectionDao.insert(apCollection);
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        } else {
            cacheService.sRemove(redis_key + entryId, userId);
            apCollectionDao.deleteById(entryId);
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
    }

    //加载文章行为
    @PostMapping("article/load_article_behavior")
    public ResponseResult articleLoadArticleBehavior(@RequestBody LoadArticleBehaviorDto dto) {
//        判断当前用户是否已经
        String likes_behavior_redis_key = "heima:likes_behavior:";
        String un_likes_behavior_redis_key = "heima:un_likes_behavior:";
        String collection_behavior_redis_key = "heima:collection_behavior:";
        String user_follow_redis_key = "heima:user_follow:";


        //1.获取作者id和文章id
        String articleId = "";

        String authorId = "";
        if (!StringUtils.isEmpty(dto.getAuthorId())) {
            authorId = dto.getAuthorId() + "";
        }

//        2.获取当前登录用户
        ApUser user = AppThreadLocalUtil.getUser();
        String userId = user.getId() + "";
        if (!StringUtils.isEmpty(dto.getArticleId())) {
            articleId = dto.getArticleId() + "";
        }

//        2.当前登录用户是否关注该文章的作者
        Boolean isfollow = cacheService.sIsMember(user_follow_redis_key + authorId, userId);

//        当前登录用户是否收藏了此文章
        Boolean iscollection = cacheService.sIsMember(collection_behavior_redis_key + articleId, userId);

//        当前登录用户是否点赞了文章
        Boolean islike = cacheService.sIsMember(likes_behavior_redis_key + articleId, userId);

//        当前登录用户是否不喜欢该文章
        Boolean isunlike = cacheService.sIsMember(un_likes_behavior_redis_key + articleId, userId);


        Map<String, Object> map = new HashMap<>();
        map.put("isfollow", isfollow);
        map.put("iscollection", iscollection);
        map.put("islike", islike);
        map.put("isunlike", isunlike);

        return ResponseResult.okResult(map);
    }
}
