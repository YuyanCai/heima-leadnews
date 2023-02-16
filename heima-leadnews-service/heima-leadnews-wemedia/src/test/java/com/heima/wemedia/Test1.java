package com.heima.wemedia;

import com.google.j2objc.annotations.AutoreleasePool;
import com.heima.common.aip.GreenImageScan;
import com.heima.common.aip.GreenTextScan;
import com.heima.file.service.FileStorageService;
import com.heima.wemedia.service.WmNewsAutoScanService;
import com.sun.org.apache.xerces.internal.xs.datatypes.ByteList;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author: xiaocai
 * @since: 2023/02/16/09:47
 */
@SpringBootTest(classes = WemediaApplication.class)
@RunWith(SpringRunner.class)
public class Test1 {

    @Autowired
    private GreenImageScan greenImageScan;

    @Autowired
    private GreenTextScan greenTextScan;

    @Autowired
    private FileStorageService fileStorageService;


    @Test
    public void testScanImage() {
        byte[] bytes = fileStorageService.downLoadFile("http://localhost:9000/leadnews/2023/02/14/7d40442776bb48e0b0635e3ca37b511f.png");
        byte[] bytes1 = fileStorageService.downLoadFile("http://localhost:9000/leadnews/2023/02/14/7d40442776bb48e0b0635e3ca37b511f.png");
        List<byte[]> byteList = new ArrayList<>();
        byteList.add(bytes);
        byteList.add(bytes1);
        for (byte[] bytes2 : byteList) {
            Map<String, String> map = greenImageScan.imageScan(bytes2);
            System.out.println(map);
        }
    }

    @Test
    public void testScanContent() {
        Map<String, String> map = greenTextScan.testScan("我是习近平");
        System.out.println(map);
    }

    @Autowired
    private WmNewsAutoScanService wmNewsAutoScanService;

    @Test
    public void autoScanWmNews() {

        wmNewsAutoScanService.autoScanWmNews(6232);
    }

    @Test
    public void testOCR() {
        try {
//创建实例
            ITesseract tesseract = new Tesseract();

            tesseract.setDatapath("/Users/xiaocai/Documents/JavaResource/Code/SpringCode/project-training/heima-leadnews/heima-leadnews");

            tesseract.setLanguage("chi_sim");

            File file = new File("/Users/xiaocai/Documents/JavaResource/Code/SpringCode/project-training/heima-leadnews/heima-leadnews/image-20210524161243572.png");
            String result = tesseract.doOCR(file);

            System.out.println(result);
        } catch (TesseractException e) {
            throw new RuntimeException(e);
        }
    }


}
