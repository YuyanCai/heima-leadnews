package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.article.IArticleClient;
import com.heima.common.aip.GreenImageScan;
import com.heima.common.aip.GreenTextScan;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.mapper.WmUserMapper;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.heima.wemedia.service.WmUserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import sun.misc.VM;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author: xiaocai
 * @since: 2023/02/16/17:15
 */
@Service
public class WmNewsAutoScanServiceImpl implements WmNewsAutoScanService {

    @Autowired
    private WmNewsMapper wmNewsMapper;

    @Autowired
    private IArticleClient iArticleClient;

    @Autowired
    private WmChannelMapper wmChannelMapper;

    @Autowired
    private WmUserMapper wmUserMapper;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private GreenTextScan greenTextScan;

    @Autowired
    private GreenImageScan greenImageScan;

    @Autowired
    WmSensitiveMapper wmSensitiveMapper;

    /**
     * 自媒体文章审核
     *
     * @param id 自媒体文章id
     */
    @Override
    @Async//标明当前方法是一个异步方法
    public void autoScanWmNews(Integer id) {
//        1、查询自媒体文章
        WmNews wmNews = wmNewsMapper.selectById(id);
        if (wmNews == null) {
            throw new RuntimeException("WmNewsAutoScanServiceImpl-文章不存在");
        }
//        从内容中提取纯文本内容和图片
        if (wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {  //SUBMIT=1=待审核
            Map<String, Object> textAndImages = handleTextAndImages(wmNews);

//            TODO 自管理敏感词
            boolean sensitiveCheck = handleSensitiveScan(textAndImages, wmNews);
            if (!sensitiveCheck) return;

//         2、审核文本内容，百度云接口
            boolean textCheck = handleScanText(wmNews, textAndImages);
            if (!textCheck) return;

//        3、审核图片的内容，百度云接口
            boolean imgCheck = handleScanImg(textAndImages, wmNews);
            if (!imgCheck) return;

//        4、审核成功，保存app端的相关数据
            ResponseResult responseResult = saveAppArticle(wmNews);
            if (!responseResult.getCode().equals(200)) {
                throw new RuntimeException("WmNewsAutoScanServiceImpl-文章审核，保存app端相关文章数据失败");
            }

            //回填article_id
            wmNews.setArticleId((Long) responseResult.getData());
            updateWmnews(wmNews, (short) 9, "审核成功");

        }
    }

    private boolean handleSensitiveScan(Map<String, Object> textAndImages, WmNews wmNews) {
        String content = (String) textAndImages.get("content");
        boolean flag = true;
        List<WmSensitive> wmSensitives = wmSensitiveMapper.selectList(Wrappers.<WmSensitive>lambdaQuery().select(WmSensitive::getSensitives));
        List<String> sensitiveList = wmSensitives.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());
        SensitiveWordUtil.initMap(sensitiveList);
//            查看文章中是否含有敏感词
        Map<String, Integer> map = SensitiveWordUtil.matchWords(content);

        if (map.size() > 0) {
            updateWmnews(wmNews, (short) 2, "该文章存在违规内容" + map);
            flag = false;
        }
        return flag;
    }

    private ResponseResult saveAppArticle(WmNews wmNews) {
        ArticleDto dto = new ArticleDto();
        BeanUtils.copyProperties(wmNews, dto);
        dto.setLayout(wmNews.getType());
        WmChannel wmChannel = wmChannelMapper.selectById(dto.getChannelId());

//            频道
        if (wmChannel != null) {
            dto.setChannelName(wmChannel.getName());
        }

//            作者
        dto.setAuthorId(wmNews.getUserId().longValue());
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if (wmUser != null) {
            dto.setAuthorName(wmUser.getName());
        }

        //设置文章id
        if (wmNews.getArticleId() != null) {
            dto.setId(wmNews.getArticleId());
        }
        dto.setCreatedTime(new Date());
        ResponseResult responseResult = iArticleClient.saveArticle(dto);
        return responseResult;
    }

    private boolean handleScanImg(Map<String, Object> textAndImages, WmNews wmNews) {
        List<String> images = (List<String>) textAndImages.get("images");
        List<String> imgUrlList = images.stream().distinct().collect(Collectors.toList());
        boolean flag = true;
        List<byte[]> imgList = new ArrayList<>();
        for (String img : imgUrlList) {
            byte[] bytes = fileStorageService.downLoadFile(img);
            imgList.add(bytes);
        }
        for (byte[] bytes : imgList) {
            Map<String, String> map = greenImageScan.imageScan(bytes);
            if (!map.get("conclusion").equals("合规")) {
                flag = false;
                updateWmnews(wmNews, (short) 3, map.get("msg"));
            }
        }
        return flag;
    }

    private boolean handleScanText(WmNews wmNews, Map<String, Object> textAndImages) {
        String content = (String) textAndImages.get("content");
        Map<String, String> map = greenTextScan.testScan(content);
        boolean flag = true;
        if (!map.get("conclusion").equals("合规")) {
            updateWmnews(wmNews, (short) 3, map.get("msg"));
            flag = false;
            return flag;
        }
        return flag;
    }

    private void updateWmnews(WmNews wmNews, short status, String reason) {
        wmNews.setStatus(status);
        wmNews.setReason(reason);
        wmNewsMapper.updateById(wmNews);
    }

    /**
     * 获得文本、图片、封面图
     *
     * @param wmNews
     * @return
     */
    private Map<String, Object> handleTextAndImages(WmNews wmNews) {
        StringBuilder stringBuilder = new StringBuilder();

        List<String> images = new ArrayList<>();

        if (!StringUtils.isEmpty(wmNews.getContent())) {
            List<Map> maps = JSONArray.parseArray(wmNews.getContent(), Map.class);
            for (Map map : maps) {
                if (map.get("type").equals("text")) {
                    stringBuilder.append(map.get("value"));
                }

                if (map.get("type").equals("image")) {
                    images.add((String) map.get("value"));
                }

            }

        }

        if (!StringUtils.isEmpty(wmNews.getImages())) {
            String[] split = wmNews.getImages().split(",");
            images.addAll(Arrays.asList(split));
        }

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("content", stringBuilder.toString());
        resultMap.put("images", images);
        return resultMap;
    }


}
