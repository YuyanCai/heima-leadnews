package com.heima.wemedia.feign;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.apis.article.IArticleClient;
import com.heima.apis.wemedia.INewsClient;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.PageResponseResult;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.AdminNewsPageReqDto;
import com.heima.model.wemedia.dtos.AdminNewsReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


/**
 * @author: xiaocai
 * @since: 2023/04/06/16:36
 */
@RestController
public class NewsClient implements INewsClient {

    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Autowired
    private WmUserMapper wmUserMapper;

    @Resource
    private IArticleClient articleClient;

    @Override
    @PostMapping("/api/v1/news/list_vo")
    public PageResponseResult listNews(AdminNewsPageReqDto dto) {
//        1.构造条件
        QueryWrapper<WmNews> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(dto.getStatus())){
            wrapper.eq("status",dto.getStatus());
        }
        if (!StringUtils.isEmpty(dto.getTitle())){
            wrapper.like("title",dto.getTitle());
        }

//        2.按创建时间倒序
        wrapper.orderByDesc("created_time");

//        3.构造分页对象
        IPage<WmNews> page = new Page<>(dto.getPage(),dto.getSize());
//        4.多条件分页模糊查询
        page = wmNewsMapper.selectPage(page, wrapper);
        PageResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        List<WmNews> records = page.getRecords();
        for (WmNews record : records) {
            WmUser wmUser = wmUserMapper.selectById(record.getUserId());
            String authorName = wmUser.getName();
            record.setAuthorName(authorName);
        }
        responseResult.setData(records);
        return responseResult;
    }

    @Override
    @GetMapping("/api/v1/news/one_vo/{id}")
    public ResponseResult detailNews(String id) {
        WmNews wmNews = wmNewsMapper.selectById(id);

        if (wmNews != null){
            WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
            wmNews.setAuthorName(wmUser.getName());
            return ResponseResult.okResult(wmNews);
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
    }

    @Override
    @PostMapping("/api/v1/news/auth_fail")
    public ResponseResult authFail(AdminNewsReqDto dto) {
        QueryWrapper<WmNews> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(dto.getId())){
            wrapper.eq("id",dto.getId());
        }

        String reason = dto.getMsg();
        WmNews wmNews = new WmNews();
        Short status = 2;
        wmNews.setStatus(status);
        wmNews.setReason(reason);
        int update = wmNewsMapper.update(wmNews, wrapper);
        if (update <= 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR);
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    @PostMapping("/api/v1/news/auth_pass")
    @Transactional
    public ResponseResult authPass(AdminNewsReqDto dto) {

        //修改文章状态
        Integer newsId = dto.getId();
        Short status = 9;
        WmNews wmNews = wmNewsMapper.selectById(newsId);
        wmNews.setStatus(status);
        wmNewsMapper.updateById(wmNews);

//        app端创建文章
        ArticleDto articleDto = new ArticleDto();
        BeanUtils.copyProperties(wmNews,articleDto);
        articleClient.saveArticle(articleDto);

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
