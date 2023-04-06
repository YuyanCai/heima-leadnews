package com.heima.wemedia.feign;

import com.heima.apis.wemedia.ISensitiveClient;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmSensitiveDto;
import com.heima.model.wemedia.dtos.WmSensitivePageReqDto;
import com.heima.wemedia.service.WmSensitiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @author: xiaocai
 * @since: 2023/04/03/20:28
 */
@RestController
public class SensitiveClient implements ISensitiveClient {

    @Autowired
    private WmSensitiveService wmSensitiveService;

    @Override
    @DeleteMapping("/api/v1/sensitive/del/{id}")
    public ResponseResult delSensitive(@PathVariable("id") String id) {
        return wmSensitiveService.delSensitive(id);
    }

    @Override
    @PostMapping("/api/v1/sensitive/list")
    public PageResponseResult listSensitive(@RequestBody WmSensitivePageReqDto wmSensitivePageReqDto){
        return wmSensitiveService.listSensitive(wmSensitivePageReqDto);
    }

    @Override
    @PostMapping("/api/v1/sensitive/save")
    public ResponseResult saveSensitive(@RequestBody WmSensitiveDto wmSensitiveDto) {
        return wmSensitiveService.saveSensitive(wmSensitiveDto);
    }

    @Override
    @PostMapping("/api/v1/sensitive/update")
    public ResponseResult updateSensitive(@RequestBody WmSensitiveDto wmSensitiveDto) {
        return wmSensitiveService.updateSensitive(wmSensitiveDto);
    }
}
