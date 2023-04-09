package com.heima.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.user.pojos.ApUserFollow;
import org.apache.ibatis.annotations.Mapper;


/**
 * APP用户关注信息表(ApUserFollow)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-08 20:02:47
 */
@Mapper
public interface ApUserFollowDao extends BaseMapper<ApUserFollow> {

}

