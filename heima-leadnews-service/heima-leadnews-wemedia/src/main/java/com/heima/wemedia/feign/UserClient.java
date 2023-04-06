package com.heima.wemedia.feign;

import com.heima.apis.wemedia.IUserClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.mapper.WmUserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: xiaocai
 * @since: 2023/04/06/10:28
 */
@RestController
public class UserClient implements IUserClient {

    @Autowired
    private WmUserMapper wmUserMapper;

    @Override
    @PostMapping("/api/v1/user/save")
    public ResponseResult saveWmUser(WmUser wmUser) {
        int insert = wmUserMapper.insert(wmUser);
        if (insert > 0) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

}
