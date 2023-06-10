package com.heima.user.pojos;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import javax.validation.constraints.NotNull;

import java.io.Serializable;

import java.util.Date;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

/**
* APP用户关注信息表
* @TableName ap_user_follow
*/
@Data
@TableName("ap_user_follow")
public class ApUserFollow implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
    * 主键
    */
    @NotNull(message="[主键]不能为空")
    @ApiModelProperty("主键")
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;
    /**
    * 用户ID
    */
    @ApiModelProperty("用户ID")
    private Integer userId;
    /**
    * 关注作者ID
    */
    @ApiModelProperty("关注作者ID")
    private Integer followId;
    /**
    * 粉丝昵称
    */
    @Size(max= 20,message="编码长度不能超过20")
    @ApiModelProperty("粉丝昵称")
    @Length(max= 20,message="编码长度不能超过20")
    private String followName;
    /**
    * 关注度
            0 偶尔感兴趣
            1 一般
            2 经常
            3 高度
    */
    @ApiModelProperty
    private Integer level;
    /**
    * 是否动态通知
    */
    @ApiModelProperty("是否动态通知")
    private Integer isNotice;
    /**
    * 创建时间
    */
    @ApiModelProperty("创建时间")
    private Date createdTime;


}
