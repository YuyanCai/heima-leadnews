package com.heima.common.aip;

import com.baidu.aip.contentcensor.AipContentCensor;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import java.util.HashMap;

import java.util.Map;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "aip")
public class GreenImageScan {
    //设置APPID/AK/SK
    private String APP_ID;
    private String API_KEY;
    private String SECRET_KEY;

    public Map<String, String> imageScan(byte[] imgByte) {
        // 初始化一个AipContentCensor
        AipContentCensor client = new AipContentCensor(APP_ID, API_KEY, SECRET_KEY);
        Map<String, String> resultMap = new HashMap<>();
        JSONObject res = client.imageCensorUserDefined(imgByte, null);
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