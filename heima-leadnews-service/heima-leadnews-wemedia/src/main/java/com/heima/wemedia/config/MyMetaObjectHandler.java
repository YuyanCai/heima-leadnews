package com.heima.wemedia.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author: xiaocai
 * @since: 2023/04/04/17:39
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    //插入的时候填充创建和修改字段
    @Override
    public void insertFill(MetaObject metaObject) {
        //属性名称，不是字段名称
        this.setFieldValByName("createdTime",new Date(),metaObject);
//        this.setFieldValByName("gmtModified",new Date(),metaObject);
    }

    //修改的时候填充修改字段
    @Override
    public void updateFill(MetaObject metaObject) {
//        this.setFieldValByName("gmtModified",new Date(),metaObject);
    }
}