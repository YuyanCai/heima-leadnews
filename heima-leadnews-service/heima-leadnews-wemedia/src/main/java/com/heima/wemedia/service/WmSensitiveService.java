package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmSensitiveDto;
import com.heima.model.wemedia.dtos.WmSensitivePageReqDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmSensitive;

public interface WmSensitiveService extends IService<WmSensitive> {

    /**
     * 敏感词CRUD
     * @return
     */
    public ResponseResult delSensitive(String id);

    public ResponseResult listSensitive(WmSensitivePageReqDto wmSensitivePageReqDto);

    public ResponseResult updateSensitive(WmSensitiveDto wmSensitiveDto);

    public ResponseResult saveSensitive(WmSensitiveDto wmSensitiveDto);


}