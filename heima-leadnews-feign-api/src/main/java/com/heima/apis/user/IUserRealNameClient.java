package com.heima.apis.user;

import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.ApUserReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: xiaocai
 * @since: 2023/04/04/18:43
 */
@FeignClient(value = "leadnews-user")
public interface IUserRealNameClient {

    /**
     * 查询列表
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/auth/list")
    public PageResponseResult listUser(@RequestBody ApUserReqDto dto);

    /**
     * 审核失败
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/auth/authFail")
    public ResponseResult auditUserFail(@RequestBody ApUserReqDto dto);

    /**
     * 审核成功
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/auth/authPass")
    public ResponseResult auditUserSuccess(@RequestBody ApUserReqDto dto);
}
