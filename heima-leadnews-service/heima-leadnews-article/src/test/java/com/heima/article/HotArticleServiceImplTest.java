package com.heima.article;

import com.heima.article.service.HotArticleService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @author: xiaocai
 * @since: 2023/06/11/12:00
 */
@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class HotArticleServiceImplTest {

    @Autowired
    private HotArticleService hotArticleService;

    @Test
    public void computeHotArticle() {
        hotArticleService.computeHotArticle();
    }
}
