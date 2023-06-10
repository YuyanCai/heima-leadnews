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
* APP用户粉丝信息表
* @TableName ap_user_fan
*/
@Data
@TableName("ap_user_fan")
public class ApUserFan implements Serializable {

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
    * 粉丝ID
    */
    @ApiModelProperty("粉丝ID")
    private Integer fansId;
    /**
    * 粉丝昵称
    */
    @Size(max= 20,message="编码长度不能超过20")
    @ApiModelProperty("粉丝昵称")
    @Length(max= 20,message="编码长度不能超过20")
    private String fansName;
    /**
    * 粉丝忠实度
            0 正常
            1 潜力股
            2 勇士
            3 铁杆
            4 老铁
    */
    @ApiModelProperty
    private Integer level;
    /**
    * 创建时间
    */
    @ApiModelProperty("创建时间")
    private Date createdTime;
    /**
    * 是否可见我动态
    */
    @ApiModelProperty("是否可见我动态")
    private Integer isDisplay;
    /**
    * 是否屏蔽私信
    */
    @ApiModelProperty("是否屏蔽私信")
    private Integer isShieldLetter;
    /**
    * 是否屏蔽评论
    */
    @ApiModelProperty("是否屏蔽评论")
    private Integer isShieldComment;


}
