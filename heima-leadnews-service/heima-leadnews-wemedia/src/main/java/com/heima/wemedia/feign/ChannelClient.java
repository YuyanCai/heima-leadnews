package com.heima.wemedia.feign;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.apis.wemedia.IChannelClient;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmChannelDto;
import com.heima.model.wemedia.dtos.WmChannelPageReqDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: xiaocai
 * @since: 2023/04/04/19:36
 */
@RestController
public class ChannelClient implements IChannelClient {
    @Autowired
    private WmChannelMapper wmChannelMapper;


    @Override
    @DeleteMapping("/api/v1/channel/del/{id}")
    public ResponseResult delChannel(@PathVariable("id") String id) {
        int i = wmChannelMapper.deleteById(id);
        if (i > 0) {
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

    @Override
    @PostMapping("/api/v1/channel/list")
    public PageResponseResult listChannel(WmChannelPageReqDto dto) {
        //1.检查参数
        //分页检查
//        dto.checkParam();

        //2.分页条件查询
        IPage page = new Page(dto.getPage(), dto.getSize());
        QueryWrapper<WmChannel> wrapper = new QueryWrapper<>();

        if (!dto.getName().isEmpty()) {
            wrapper.like("name", dto.getName());
        }
        wrapper.orderByDesc("created_time");

        IPage iPage = wmChannelMapper.selectPage(page, wrapper);

        //3.结果返回
        PageResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) iPage.getTotal());
        responseResult.setData(iPage.getRecords());

        return responseResult;
    }

    @Override
    @PostMapping("/api/v1/channel/save")
    public ResponseResult saveChannel(WmChannelDto wmChannelDto) {
        WmChannel wmChannel = new WmChannel();
        BeanUtils.copyProperties(wmChannelDto, wmChannel);
        int insert = wmChannelMapper.insert(wmChannel);
        if (insert <= 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    @PostMapping("/api/v1/channel/update")
    public ResponseResult updateChannel(WmChannelDto wmChannelDto) {
        WmChannel wmChannel = new WmChannel();
        BeanUtils.copyProperties(wmChannelDto, wmChannel);
        QueryWrapper<WmChannel> wrapper = new QueryWrapper<>();
        wrapper.eq("id", wmChannel.getId());
        int update = wmChannelMapper.update(wmChannel, wrapper);
        if (update <= 0) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
