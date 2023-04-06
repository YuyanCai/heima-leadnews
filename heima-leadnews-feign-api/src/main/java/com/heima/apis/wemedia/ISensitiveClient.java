package com.heima.apis.wemedia;

import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmSensitiveDto;
import com.heima.model.wemedia.dtos.WmSensitivePageReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: xiaocai
 * @since: 2023/04/03/20:01
 */
@FeignClient(value = "leadnews-wemedia")
public interface ISensitiveClient {
    @DeleteMapping("/api/v1/sensitive/del/{id}")
    public ResponseResult delSensitive(@PathVariable("id") String id);

    @PostMapping("/api/v1/sensitive/list")
    public PageResponseResult listSensitive(@RequestBody WmSensitivePageReqDto wmSensitivePageReqDto);

    @PostMapping("/api/v1/sensitive/save")
    public ResponseResult saveSensitive(@RequestBody WmSensitiveDto wmSensitiveDto);

    @PostMapping("/api/v1/sensitive/update")
    public ResponseResult updateSensitive(@RequestBody WmSensitiveDto wmSensitiveDto);
}
