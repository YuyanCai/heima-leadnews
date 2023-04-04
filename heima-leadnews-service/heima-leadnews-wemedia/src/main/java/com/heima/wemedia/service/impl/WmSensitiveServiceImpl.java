package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmSensitiveDto;
import com.heima.model.wemedia.dtos.WmSensitivePageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.WmSensitiveService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author: xiaocai
 * @since: 2023/04/03/20:36
 */
@Service
public class WmSensitiveServiceImpl extends ServiceImpl<WmSensitiveMapper, WmSensitive> implements WmSensitiveService {

    @Override
    public ResponseResult delSensitive(String id) {
        boolean b = removeById(id);
        if (b) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

    @Override
    public ResponseResult listSensitive(WmSensitivePageReqDto dto) {
        //1.检查参数
        //分页检查
        dto.checkParam();

        //2.分页条件查询
        IPage page = new Page(dto.getPage(), dto.getSize());
        QueryWrapper<WmSensitive> wrapper = new QueryWrapper<>();

        if (!dto.getName().isEmpty()){
            wrapper.eq("sensitives", dto.getName());
        }
        wrapper.orderByDesc("created_time");
        page = page(page, wrapper);
        //3.结果返回
        ResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());

        return responseResult;
    }

    @Override
    public ResponseResult updateSensitive(WmSensitiveDto wmSensitiveDto) {
        WmSensitive wmSensitive = new WmSensitive();
        BeanUtils.copyProperties(wmSensitiveDto,wmSensitive);
        QueryWrapper<WmSensitive> wrapper = new QueryWrapper<>();
        wrapper.eq("id",wmSensitiveDto.getId());
        boolean update = update(wmSensitive, wrapper);
        if (!update){
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult saveSensitive(WmSensitiveDto wmSensitiveDto) {
        WmSensitive wmSensitive = new WmSensitive();
        BeanUtils.copyProperties(wmSensitiveDto,wmSensitive);
        boolean save = save(wmSensitive);
        if (!save){
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
