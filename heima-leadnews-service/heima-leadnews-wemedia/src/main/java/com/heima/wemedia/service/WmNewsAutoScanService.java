package com.heima.wemedia.service;

/**
 * @author: xiaocai
 * @since: 2023/02/16/17:15
 */
public interface WmNewsAutoScanService {
    /**
     * 自媒体文章审核
     * @param id  自媒体文章id
     */
    public void autoScanWmNews(Integer id);
}
