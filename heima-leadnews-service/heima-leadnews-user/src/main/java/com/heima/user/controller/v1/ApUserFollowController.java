package com.heima.user.controller.v1;

import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.UserFollowBehaviorDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.mapper.ApUserFanDao;
import com.heima.user.mapper.ApUserFollowDao;
import com.heima.user.pojos.ApUserFan;
import com.heima.user.pojos.ApUserFollow;
import com.heima.utils.thread.AppThreadLocalUtil;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: xiaocai
 * @since: 2023/04/08/19:55
 */

@RestController
@RequestMapping("/api/v1/user")
@Api(value = "app端用户关注", tags = "app端用户关注和取消关注")
public class ApUserFollowController {

    @Autowired
    private CacheService cacheService;

    @Autowired
    private ApUserFanDao apUserFanDao;

    @Autowired
    private ApUserFollowDao apUserFollowDao;

    //关注和取消关注
    @PostMapping("user_follow")
    @Transactional
    public ResponseResult userFollow(@RequestBody UserFollowBehaviorDto dto) {
        String redis_key = "heima:user_follow:";
        //1.获取用户id和文章id
        String authorId = "";
        ApUser user = AppThreadLocalUtil.getUser();
        String userId = user.getId() + "";
        if (!StringUtils.isEmpty(dto.getAuthorId())) {
            authorId = dto.getAuthorId() + "";
        }

        if (dto.getOperation() == 0) {
            cacheService.sAdd(redis_key + authorId, userId);
//            粉丝表
            ApUserFan apUserFan = new ApUserFan();
            apUserFan.setFansName(user.getName());
            apUserFan.setFansId(user.getId());
            apUserFan.setUserId(dto.getAuthorId());
            apUserFanDao.insert(apUserFan);

//            关注表
            ApUserFollow apUserFollow = new ApUserFollow();
            apUserFollow.setUserId(user.getId());
            apUserFollow.setFollowId(dto.getAuthorId());
            apUserFollowDao.insert(apUserFollow);

            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        } else {
            cacheService.sRemove(redis_key + authorId, userId);
            apUserFanDao.deleteById(user.getId());
            apUserFollowDao.deleteById(dto.getAuthorId());
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

    }


}
