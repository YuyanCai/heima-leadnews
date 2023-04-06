package com.heima.apis.wemedia;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: xiaocai
 * @since: 2023/04/06/10:27
 */
@FeignClient(value = "leadnews-wemedia")
public interface IUserClient {

    @PostMapping("/api/v1/user/save")
    public ResponseResult saveWmUser(@RequestBody WmUser wmUser);
}
