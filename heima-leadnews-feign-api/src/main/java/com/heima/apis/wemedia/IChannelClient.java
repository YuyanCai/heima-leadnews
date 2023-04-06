package com.heima.apis.wemedia;

import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmChannelDto;
import com.heima.model.wemedia.dtos.WmChannelPageReqDto;
import com.heima.model.wemedia.pojos.WmUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: xiaocai
 * @since: 2023/04/04/18:43
 */
@FeignClient(value = "leadnews-wemedia")
public interface IChannelClient {

    @DeleteMapping("/api/v1/channel/del/{id}")
    public ResponseResult delChannel(@PathVariable("id") String id);

    @PostMapping("/api/v1/channel/list")
    public PageResponseResult listChannel(@RequestBody WmChannelPageReqDto wmChannelPageReqDto);

    @PostMapping("/api/v1/channel/save")
    public ResponseResult saveChannel(@RequestBody WmChannelDto wmChannelDto);

    @PostMapping("/api/v1/channel/update")
    public ResponseResult updateChannel(@RequestBody WmChannelDto wmChannelDto);
}
