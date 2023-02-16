package com.heima.common.aip;

import com.alibaba.fastjson.JSON;
import com.aliyuncs.DefaultAcsClient;
import com.aliyuncs.IAcsClient;
import com.aliyuncs.exceptions.ClientException;
import com.aliyuncs.exceptions.ServerException;
import com.aliyuncs.green.model.v20180509.TextScanRequest;
import com.aliyuncs.http.FormatType;
import com.aliyuncs.http.HttpResponse;
import com.aliyuncs.profile.DefaultProfile;
import com.aliyuncs.profile.IClientProfile;
import com.baidu.aip.contentcensor.AipContentCensor;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.*;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aip")
public class GreenTextScan {
    //设置APPID/AK/SK
    private String APP_ID;
    private String API_KEY;
    private String SECRET_KEY;

    @Autowired
    private Gson gson;

    public Map<String, String> testScan(String content) {
        // 初始化一个AipContentCensor
        AipContentCensor client = new AipContentCensor(APP_ID, API_KEY, SECRET_KEY);
        Map<String, String> resultMap = new HashMap<>();
        JSONObject res = client.textCensorUserDefined(content);
        System.out.println(res.toString(2));
        //返回的响应结果
        Map<String, Object> map = res.toMap();
//        获得特殊字段
        String conclusion = (String) map.get("conclusion");

        if (conclusion.equals("合规")) {
            resultMap.put("conclusion", conclusion);
            return resultMap;
        }
//        获得特殊集合字段
        JSONArray dataArrays = res.getJSONArray("data");
        String msg = "";
        for (Object result : dataArrays) {
            //获得原因
            msg = ((JSONObject) result).getString("msg");
        }

        resultMap.put("conclusion", "不合规");
        resultMap.put("msg", msg);
        return resultMap;
    }
}