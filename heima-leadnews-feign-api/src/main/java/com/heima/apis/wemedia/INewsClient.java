package com.heima.apis.wemedia;

import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.AdminNewsPageReqDto;
import com.heima.model.wemedia.dtos.AdminNewsReqDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author: xiaocai
 * @since: 2023/04/06/16:34
 */
@FeignClient(value = "leadnews-wemedia")
public interface INewsClient {

    /**
     * 查询文章列表
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/news/list_vo")
    public PageResponseResult listNews(@RequestBody AdminNewsPageReqDto dto);

    /**
     * 查询文章详情
     * @param id
     * @return
     */
    @GetMapping("/api/v1/news/one_vo/{id}")
    public ResponseResult detailNews(@PathVariable("id") String id);

    /**
     * 审核失败controller
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/news/auth_fail")
    public ResponseResult authFail(@RequestBody AdminNewsReqDto dto);

    /**
     * 审核成功controller
     * @param dto
     * @return
     */
    @PostMapping("/api/v1/news/auth_pass")
    public ResponseResult authPass(@RequestBody AdminNewsReqDto dto);
}
