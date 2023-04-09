package com.heima.article.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

/**
* APP收藏信息表
* @TableName ap_collection
*/
@Data
@TableName("ap_collection")
public class ApCollection implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @NotNull(message = "[]不能为空")
    @ApiModelProperty("")
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    /**
     * 实体ID
     */
    @ApiModelProperty("实体ID")
    private Integer entryId;
    /**
     * 文章ID
     */
    @ApiModelProperty("文章ID")
    private Long articleId;
    /**
     * 点赞内容类型
     * 0文章
     * 1动态
     */
    @ApiModelProperty("点赞内容类型 0文章 1动态")
    private Integer type;
    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    private Date collectionTime;
    /**
     * 发布时间
     */
    @ApiModelProperty("发布时间")
    private Date publishedTime;
}