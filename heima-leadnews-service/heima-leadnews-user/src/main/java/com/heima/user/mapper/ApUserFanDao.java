package com.heima.user.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.user.pojos.ApUserFan;
import org.apache.ibatis.annotations.Mapper;

/**
 * APP用户粉丝信息表(ApUserFan)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-08 19:59:41
 */
@Mapper
public interface ApUserFanDao extends BaseMapper<ApUserFan> {

}

