package com.heima.article.mapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.article.entity.ApCollection;
import org.apache.ibatis.annotations.Mapper;

/**
 * APP收藏信息表(ApCollection)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-08 22:03:08
 */
@Mapper
public interface ApCollectionDao extends BaseMapper<ApCollection> {

}

