## 前言
联系我：2350938432

## 环境搭建

### 项目结构

![image-20230208212329899](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230208212329899.png)

### 登录功能

- 用户点击**开始使用**

  登录后的用户权限较大，可以查看，也可以操作（点赞，关注，评论）

- 用户点击**不登录，先看看**

​       游客只有查看的权限

#### 代码编写思路

1. 实体类

通过Mybatisplus逆向生成实体类，也可以用代码生成器生成

2. dao

自己写，或者通过Mybatisplus逆向生成实体类，也可以用代码生成器生成

3. service

业务类，根据需求写，当然它也可以通过工具自动生成

4. controller

控制类，根据需求写，也可以通过工具自动生成

#### 对密码验证进行加盐

**什么是加盐？**

就是单对密码加密太容易被破解，所以在密码的基础上加一些字符串，对加过字符串的密码进行md5加密，这个过程就叫加盐。

**注册流程如下：**

![image-20230209160520930](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230209160520930.png)

**登录流程如下：**

![image-20230209160541439](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230209160541439.png)



#### 思路分析

![image-20230209160630974](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230209160630974.png)

1，用户输入了用户名和密码进行登录，校验成功后返回jwt(基于当前用户的id生成)

2，用户游客登录，生成jwt返回(基于默认值0生成)

#### 关键代码

> md5加密相同的字符，得到的结果是一样的

```java
@Service
public class ApUserServiceImpl extends ServiceImpl<ApUserMapper, ApUser> implements ApUserService {

    @Override
    public ResponseResult login(LoginDto dto) {

        //1.正常登录（手机号+密码登录）
        if (!StringUtils.isBlank(dto.getPhone()) && !StringUtils.isBlank(dto.getPassword())) {
            //1.1查询用户
            ApUser apUser = getOne(Wrappers.<ApUser>lambdaQuery().eq(ApUser::getPhone, dto.getPhone()));
            if (apUser == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST,"用户不存在");
            }

            //1.2 比对密码
            String salt = apUser.getSalt();
            String pswd = dto.getPassword();
            pswd = DigestUtils.md5DigestAsHex((pswd + salt).getBytes());
            if (!pswd.equals(apUser.getPassword())) {
                return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
            }
            //1.3 返回数据  jwt
            Map<String, Object> map = new HashMap<>();
            map.put("token", AppJwtUtil.getToken(apUser.getId().longValue()));
            apUser.setSalt("");
            apUser.setPassword("");
            map.put("user", apUser);
            return ResponseResult.okResult(map);
        } else {
            //2.游客  同样返回token  id = 0
            Map<String, Object> map = new HashMap<>();
            map.put("token", AppJwtUtil.getToken(0l));
            return ResponseResult.okResult(map);
        }
    }
}

控制层controller
@RestController
@RequestMapping("/api/v1/login")
public class ApUserLoginController {

    @Autowired
    private ApUserService apUserService;

    @PostMapping("/login_auth")
    public ResponseResult login(@RequestBody LoginDto dto) {
        return apUserService.login(dto);
    }
}
```

#### 测试

> 游客身份登录





> 手机号密码登录

- 不存在的用户测试登录

![image-20230209161815202](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230209161815202.png)

- 存在的用户测试

![image-20230209162327885](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230209162327885.png)

### 接口工具

#### postman

postman不在介绍

#### swagger

> 是什么？

一款接口管理工具

> 能有啥用？

后端开发完接口给前端一个swagger地址就可以了，前端根据swagger接口的规范设计页面

> 前端不能开发好页面给后端一个接口管理工具吗？

可以，常见的工具是YAPI

> swagger怎么用？

1. 引入依赖,在heima-leadnews-model和heima-leadnews-common模块中引入该依赖

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger2</artifactId>
</dependency>
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-swagger-ui</artifactId>
</dependency>
```

只需要在heima-leadnews-common中进行配置即可，因为其他微服务工程都直接或间接依赖即可。

2. 在heima-leadnews-common工程中添加一个配置类

新增：com.heima.common.swagger.SwaggerConfiguration

```java
package com.heima.common.swagger;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

   @Bean
   public Docket buildDocket() {
      return new Docket(DocumentationType.SWAGGER_2)
              .apiInfo(buildApiInfo())
              .select()
              // 要扫描的API(Controller)基础包
              .apis(RequestHandlerSelectors.basePackage("com.heima"))
              .paths(PathSelectors.any())
              .build();
   }

   private ApiInfo buildApiInfo() {
      Contact contact = new Contact("黑马程序员","","");
      return new ApiInfoBuilder()
              .title("黑马头条-平台管理API文档")
              .description("黑马头条后台api")
              .contact(contact)
              .version("1.0.0").build();
   }
}
```

3. 在heima-leadnews-common模块中的resources目录中新增以下目录和文件

文件：resources/META-INF/Spring.factories

```java
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.heima.common.swagger.SwaggerConfiguration
```

![image-20230208213334886](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230208213334886.png)

启动user微服务，访问地址：http://localhost:51801/swagger-ui.html

#### knife4j

knife4j是为Java MVC框架集成Swagger生成Api文档的增强解决方案,前身是swagger-bootstrap-ui,取名kni4j是希望它能像一把匕首一样小巧,轻量,并且功能强悍!

gitee地址：https://gitee.com/xiaoym/knife4j

官方文档：https://doc.xiaominfo.com/

效果演示：http://knife4j.xiaominfo.com/doc.html

> 核心功能

该UI增强包主要包括两大核心功能：文档说明 和 在线调试

- 文档说明：根据Swagger的规范说明，详细列出接口文档的说明，包括接口地址、类型、请求示例、请求参数、响应示例、响应参数、响应码等信息，使用swagger-bootstrap-ui能根据该文档说明，对该接口的使用情况一目了然。
- 在线调试：提供在线接口联调的强大功能，自动解析当前接口参数,同时包含表单验证，调用参数可返回接口响应内容、headers、Curl请求命令实例、响应时间、响应状态码等信息，帮助开发者在线调试，而不必通过其他测试工具测试接口是否正确,简介、强大。
- 个性化配置：通过个性化ui配置项，可自定义UI的相关显示信息
- **离线文档：根据标准规范，生成的在线markdown离线文档，开发者可以进行拷贝生成markdown接口文档，通过其他第三方markdown转换工具转换成html或pdf，这样也可以放弃swagger2markdown组件**
- **接口排序：自1.8.5后，ui支持了接口排序功能，例如一个注册功能主要包含了多个步骤,可以根据swagger-bootstrap-ui提供的接口排序规则实现接口的排序，step化接口操作，方便其他开发者进行接口对接**

> 快速使用

- common模块引入依赖

```xml
<dependency>
     <groupId>com.github.xiaoymin</groupId>
     <artifactId>knife4j-spring-boot-starter</artifactId>
</dependency>
```

- common模块创建Swagger配置文件

```java

@Configuration
@EnableSwagger2
@EnableKnife4j
@Import(BeanValidatorPluginsConfiguration.class)
public class Swagger2Configuration {

    @Bean(value = "defaultApi2")
    public Docket defaultApi2() {
        Docket docket=new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(apiInfo())
                //分组名称
                .groupName("1.0")
                .select()
                //这里指定Controller扫描包路径
                .apis(RequestHandlerSelectors.basePackage("com.heima"))
                .paths(PathSelectors.any())
                .build();
        return docket;
    }
    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("黑马头条API文档")
                .description("黑马头条API文档")
                .version("1.0")
                .build();
    }
}
```

以上有两个注解需要特别说明，如下表：

| 注解              | 说明                                                         |
| ----------------- | ------------------------------------------------------------ |
| `@EnableSwagger2` | 该注解是Springfox-swagger框架提供的使用Swagger注解，该注解必须加 |
| `@EnableKnife4j`  | 该注解是`knife4j`提供的增强注解,Ui提供了例如动态参数、参数过滤、接口排序等增强功能,如果你想使用这些增强功能就必须加该注解，否则可以不用加 |

- 添加配置

在Spring.factories中新增配置

```java
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.heima.common.swagger.Swagger2Configuration, \
  com.heima.common.swagger.SwaggerConfiguration
```

- 访问

在浏览器输入地址：`http://host:port/doc.html`

![image-20230209163051451](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230209163051451.png)

### 网关

网关的作用：

![image-20230209163413928](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230209163413928.png)

#### 全局过滤器实现jwt校验

思路分析：

1. 用户进入网关开始登陆，网关过滤器进行判断，如果是登录，则路由到后台管理微服务进行登录
2. 用户登录成功，后台管理微服务签发JWT TOKEN信息返回给用户
3. 用户再次进入网关开始访问，网关过滤器接收用户携带的TOKEN 
4. 网关过滤器解析TOKEN ，判断是否有权限，如果有，则放行，如果没有则返回未认证错误



具体实现：

第一：

​	在认证过滤器中需要用到jwt的解析，所以需要把工具类拷贝一份到网关微服务

第二：

在网关微服务中新建全局过滤器：

```java
@Component
@Slf4j
public class AuthorizeFilter implements Ordered, GlobalFilter {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        //1.获取request和response对象
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        //2.判断是否是登录
        if(request.getURI().getPath().contains("/login")){
            //放行
            return chain.filter(exchange);
        }


        //3.获取token
        String token = request.getHeaders().getFirst("token");

        //4.判断token是否存在
        if(StringUtils.isBlank(token)){
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //5.判断token是否有效
        try {
            Claims claimsBody = AppJwtUtil.getClaimsBody(token);
            //是否是过期
            int result = AppJwtUtil.verifyToken(claimsBody);
            if(result == 1 || result  == 2){
                response.setStatusCode(HttpStatus.UNAUTHORIZED);
                return response.setComplete();
            }
        }catch (Exception e){
            e.printStackTrace();
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }

        //6.放行
        return chain.filter(exchange);
    }

    /**
     * 优先级设置  值越小  优先级越高
     * @return
     */
    @Override
    public int getOrder() {
        return 0;
    }
}
```

测试：

启动user服务，继续访问其他微服务，会提示需要认证才能访问，这个时候需要在heads中设置设置token才能正常访问。

### 前端集成

通过nginx实现动静分离，就是将静态资源放到nginx的html目录，动态请求处理交给后端服务

这里又对后端服务做了反向代理，可以应对更多的场景

![image-20230209173647802](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230209173647802.png)

## app端文章查看

### 需求分析

文章布局展示

![image-20230210195739639](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230210195739639.png)

### 分库分表

本项目采用的是垂直切分，切分的策略是将常用的字段和不常用的字段进行划分，这样做就是为了提高效率

文章表被分为三个表，分别是：

**ap_article  文章基本信息表**

就是存放文章简介的，在首页显示的时候我们去查询这个表即可，因为首页只需要显示文章的基本信息！

![image-20230210203820452](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230210203820452.png)

**ap_article_config  文章配置表**

看文章是否转发、下架、已删除

![image-20230210204810064](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230210204810064.png)



**ap_article_content 文章内容表**

文章内容

![image-20230210204749231](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230210204749231.png)

**分库分表实战讲解**

https://pdai.tech/md/db/sql-mysql/sql-mysql-devide.html



### 上拉下拉查看文章

#### mapper文件

> <![CDATA[ ]]> 是什么，这是XML语法。在CDATA内部的所有内容都会被解析器忽略。
>
> 被<![CDATA[]]>这个标记所包含的内容将表示为纯文本，比如<![CDATA[<]]>表示文本内容“<”。

```xml
@Mapper
public interface ApArticleMapper extends BaseMapper<ApArticle> {

    public List<ApArticle> loadArticleList(@Param("dto")ArticleHomeDto dto,@Param("type") Short type);

}

<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.heima.article.mapper.ApArticleMapper">

    <resultMap id="resultMap" type="com.heima.model.article.pojos.ApArticle">
        <id column="id" property="id"/>
        <result column="title" property="title"/>
        <result column="author_id" property="authorId"/>
        <result column="author_name" property="authorName"/>
        <result column="channel_id" property="channelId"/>
        <result column="channel_name" property="channelName"/>
        <result column="layout" property="layout"/>
        <result column="flag" property="flag"/>
        <result column="images" property="images"/>
        <result column="labels" property="labels"/>
        <result column="likes" property="likes"/>
        <result column="collection" property="collection"/>
        <result column="comment" property="comment"/>
        <result column="views" property="views"/>
        <result column="province_id" property="provinceId"/>
        <result column="city_id" property="cityId"/>
        <result column="county_id" property="countyId"/>
        <result column="created_time" property="createdTime"/>
        <result column="publish_time" property="publishTime"/>
        <result column="sync_status" property="syncStatus"/>
        <result column="static_url" property="staticUrl"/>
    </resultMap>

    <select id="loadArticleList" resultMap="resultMap">
        SELECT
        aa.*
        FROM
        `ap_article` aa
        LEFT JOIN ap_article_config aac ON aa.id = aac.article_id
        <where>
            and aac.is_delete != 1
            and aac.is_down != 1
            <!-- loadmore -->
            <if test="type != null and type == 1">
                and aa.publish_time <![CDATA[<]]> #{dto.minBehotTime}
            </if>
            <if test="type != null and type == 2">
                and aa.publish_time <![CDATA[>]]> #{dto.maxBehotTime}
            </if>
            <if test="dto.tag != '__all__'">
                and aa.channel_id = #{dto.tag}
            </if>
        </where>
        order by aa.publish_time desc
        limit #{dto.size}
    </select>

</mapper>
```

#### Impl

```java
/**
 * @author: xiaocai
 * @since: 2023/02/10/22:45
 */
@Service
@Transactional
@Slf4j
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    private final static short MAX_PAGE_SIZE = 50;

    @Autowired
    private ApArticleMapper apArticleMapper;

    /**
     * 根据参数加载文章列表
     *
     * @param loadtype 1代表加载更多  2代表加载最新
     * @param dto
     * @return
     */
    @Override
    public ResponseResult load(Short loadtype, ArticleHomeDto dto) {
        /**
         * 1.校验参数
         */
        Integer size = dto.getSize();
        if (size == null || size == 0) {
            size = 10;
        }
        size = Math.min(size, MAX_PAGE_SIZE);
        dto.setSize(size);

//        1.1类型参数校验
        if (!loadtype.equals(ArticleConstants.LOADTYPE_LOAD_MORE) && !loadtype.equals(ArticleConstants.LOADTYPE_LOAD_NEW)){
            loadtype = ArticleConstants.LOADTYPE_LOAD_MORE;
        }

//        1.2文章频道检验
        if(StringUtils.isEmpty(dto.getTag())){
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

//        1.3时间检验
        if(dto.getMaxBehotTime() == null) dto.setMaxBehotTime(new Date());
        if(dto.getMinBehotTime() == null) dto.setMaxBehotTime(new Date());


        /**
         * 2.查询数据
         */
        List<ApArticle> apArticles = apArticleMapper.loadArticleList(dto, loadtype);

        /**
         * 3.结果封装
         */
        return ResponseResult.okResult(apArticles);
    }
}
```

### freemarker

#### 简介

FreeMarker 是一款 模板引擎： 即一种基于模板和要改变的数据， 并用来生成输出文本(HTML网页，电子邮件，配置文件，源代码等)的通用工具。 它不是面向最终用户的，而是一个Java类库，是一款程序员可以嵌入他们所开发产品的组件。

模板编写为FreeMarker Template Language (FTL)。它是简单的，专用的语言， *不是* 像PHP那样成熟的编程语言。 那就意味着要准备数据在真实编程语言中来显示，比如数据库查询和业务运算， 之后模板显示已经准备好的数据。在模板中，你可以专注于如何展现数据， 而在模板之外可以专注于要展示什么数据。 

![1528820943975](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/1528820943975.png)



常用的java模板引擎还有哪些？

Jsp、Freemarker、Thymeleaf 、Velocity 等。

1.Jsp 为 Servlet 专用，不能单独进行使用。

2.Thymeleaf 为新技术，功能较为强大，但是执行的效率比较低。

3.Velocity从2010年更新完 2.0 版本后，便没有在更新。Spring Boot 官方在 1.4 版本后对此也不在支持，虽然 Velocity 在 2017 年版本得到迭代，但为时已晚。 

#### 快速入门

freemarker作为springmvc一种视图格式，默认情况下SpringMVC支持freemarker视图格式。

需要创建Spring Boot+Freemarker工程用于测试模板。

pom.xml如下

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>heima-leadnews-test</artifactId>
        <groupId>com.heima</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>freemarker-demo</artifactId>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-freemarker</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
        <!-- lombok -->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <!-- apache 对 java io 的封装工具库 -->
        <dependency>
            <groupId>org.apache.commons</groupId>
            <artifactId>commons-io</artifactId>
            <version>1.3.2</version>
        </dependency>
    </dependencies>

</project>
```

##### 配置文件

配置application.yml

```yaml
server:
  port: 8881 #服务端口
spring:
  application:
    name: freemarker-demo #指定服务名
  freemarker:
    cache: false  #关闭模板缓存，方便测试
    settings:
      template_update_delay: 0 #检查模板更新延迟时间，设置为0表示立即检查，如果时间大于0会有缓存不方便进行模板测试
    suffix: .ftl               #指定Freemarker模板文件的后缀名
```

##### 创建模型类

在freemarker的测试工程下创建模型类型用于测试

```java
package com.heima.freemarker.entity;

import lombok.Data;

import java.util.Date;

@Data
public class Student {
    private String name;//姓名
    private int age;//年龄
    private Date birthday;//生日
    private Float money;//钱包
}
```





##### 创建模板

在resources下创建templates，此目录为freemarker的默认模板存放目录。

在templates下创建模板文件 01-basic.ftl ，模板中的插值表达式最终会被freemarker替换成具体的数据。

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Hello World!</title>
</head>
<body>
<b>普通文本 String 展示：</b><br><br>
Hello ${name} <br>
<hr>
<b>对象Student中的数据展示：</b><br/>
姓名：${stu.name}<br/>
年龄：${stu.age}
<hr>
</body>
</html>
```



##### 创建controller

创建Controller类，向Map中添加name，最后返回模板文件。

```java
package com.xuecheng.test.freemarker.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Controller
public class HelloController {

    @GetMapping("/basic")
    public String test(Model model) {


        //1.纯文本形式的参数
        model.addAttribute("name", "freemarker");
        //2.实体类相关的参数
        
        Student student = new Student();
        student.setName("小明");
        student.setAge(18);
        model.addAttribute("stu", student);

        return "01-basic";
    }
}
```

01-basic.ftl，使用插值表达式填充数据

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Hello World!</title>
</head>
<body>
<b>普通文本 String 展示：</b><br><br>
Hello ${name} <br>
<hr>
<b>对象Student中的数据展示：</b><br/>
姓名：${stu.name}<br/>
年龄：${stu.age}
<hr>
</body>
</html>
```



##### 创建启动类

```java
package com.heima.freemarker;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FreemarkerDemotApplication {
    public static void main(String[] args) {
        SpringApplication.run(FreemarkerDemotApplication.class,args);
    }
}
```

##### 测试

请求：http://localhost:8881/basic

![1576129529361](../../../Java高级/09阶段九：项目实战-黑马头条(V12.5)/02-资料/黑马头条/day02-app端文章查看，静态化freemarker,分布式文件系统minIO/讲义/app端文章查看，静态化freemarker,分布式文件系统minIO.assets/1576129529361.png)



#### freemarker基础

##### 基础语法种类

  1、注释，即<#--  -->，介于其之间的内容会被freemarker忽略

```velocity
<#--我是一个freemarker注释-->
```

  2、插值（Interpolation）：即 **`${..}`** 部分,freemarker会用真实的值代替**`${..}`**

```velocity
Hello ${name}
```

  3、FTL指令：和HTML标记类似，名字前加#予以区分，Freemarker会解析标签中的表达式或逻辑。

```velocity
<# >FTL指令</#> 
```

  4、文本，仅文本信息，这些不是freemarker的注释、插值、FTL指令的内容会被freemarker忽略解析，直接输出内容。

```velocity
<#--freemarker中的普通文本-->
我是一个普通的文本
```

##### 集合指令（List和Map）

1、数据模型：

在HelloController中新增如下方法：

```java
@GetMapping("/list")
public String list(Model model){

    //------------------------------------
    Student stu1 = new Student();
    stu1.setName("小强");
    stu1.setAge(18);
    stu1.setMoney(1000.86f);
    stu1.setBirthday(new Date());

    //小红对象模型数据
    Student stu2 = new Student();
    stu2.setName("小红");
    stu2.setMoney(200.1f);
    stu2.setAge(19);

    //将两个对象模型数据存放到List集合中
    List<Student> stus = new ArrayList<>();
    stus.add(stu1);
    stus.add(stu2);

    //向model中存放List集合数据
    model.addAttribute("stus",stus);

    //------------------------------------

    //创建Map数据
    HashMap<String,Student> stuMap = new HashMap<>();
    stuMap.put("stu1",stu1);
    stuMap.put("stu2",stu2);
    // 3.1 向model中存放Map数据
    model.addAttribute("stuMap", stuMap);

    return "02-list";
}
```

2、模板：

在templates中新增`02-list.ftl`文件

> html中的td、tr、th什么意思？

TD：英文全称是＂tabledatacell＂，中文意思是“表中的数据单元”。

TR：英文全称是＂tablerow＂的缩写”的缩写。

TH：英文全称是＂tableheadercell＂的缩写，在中文中是“表头单元格”的意思。

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Hello World!</title>
</head>
<body>
    
<#-- list 数据的展示 -->
<b>展示list中的stu数据:</b>
<br>
<br>
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
    </tr>
</table>
<hr>
    
<#-- Map 数据的展示 -->
<b>map数据的展示：</b>
<br/><br/>
<a href="###">方式一：通过map['keyname'].property</a><br/>
输出stu1的学生信息：<br/>
姓名：<br/>
年龄：<br/>
<br/>
<a href="###">方式二：通过map.keyname.property</a><br/>
输出stu2的学生信息：<br/>
姓名：<br/>
年龄：<br/>

<br/>
<a href="###">遍历map中两个学生信息：</a><br/>
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td> 
    </tr>
</table>
<hr>
 
</body>
</html>
```

实例代码：

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Hello World!</title>
</head>
<body>
    
<#-- list 数据的展示 -->
<b>展示list中的stu数据:</b>
<br>
<br>
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
    </tr>
    <#list stus as stu>
        <tr>
            <td>${stu_index+1}</td>
            <td>${stu.name}</td>
            <td>${stu.age}</td>
            <td>${stu.money}</td>
        </tr>
    </#list>

</table>
<hr>
    
<#-- Map 数据的展示 -->
<b>map数据的展示：</b>
<br/><br/>
<a href="###">方式一：通过map['keyname'].property</a><br/>
输出stu1的学生信息：<br/>
姓名：${stuMap['stu1'].name}<br/>
年龄：${stuMap['stu1'].age}<br/>
<br/>
<a href="###">方式二：通过map.keyname.property</a><br/>
输出stu2的学生信息：<br/>
姓名：${stuMap.stu2.name}<br/>
年龄：${stuMap.stu2.age}<br/>

<br/>
<a href="###">遍历map中两个学生信息：</a><br/>
<table>
    <tr>
        <td>序号</td>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
    </tr>
    <#list stuMap?keys as key >
        <tr>
            <td>${key_index}</td>
            <td>${stuMap[key].name}</td>
            <td>${stuMap[key].age}</td>
            <td>${stuMap[key].money}</td>
        </tr>
    </#list>
</table>
<hr>
 
</body>
</html>
```

👆上面代码解释：

${k_index}：
	index：得到循环的下标，使用方法是在stu后边加"_index"，它的值是从0开始



##### if指令

​	 if 指令即判断指令，是常用的FTL指令，freemarker在解析时遇到if会进行判断，条件为真则输出if中间的内容，否则跳过内容不再输出。

- 指令格式

```html
<#if ></if>
```



1、数据模型：

使用list指令中测试数据模型，判断名称为小红的数据字体显示为红色。

2、模板：

```velocity
<table>
    <tr>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
    </tr>
    <#list stus as stu>
        <tr>
            <td >${stu.name}</td>
            <td>${stu.age}</td>
            <td >${stu.mondy}</td>
        </tr>
    </#list>

</table>
```



实例代码：

```velocity
<table>
    <tr>
        <td>姓名</td>
        <td>年龄</td>
        <td>钱包</td>
    </tr>
    <#list stus as stu >
        <#if stu.name='小红'>
            <tr style="color: red">
                <td>${stu_index}</td>
                <td>${stu.name}</td>
                <td>${stu.age}</td>
                <td>${stu.money}</td>
            </tr>
            <#else >
            <tr>
                <td>${stu_index}</td>
                <td>${stu.name}</td>
                <td>${stu.age}</td>
                <td>${stu.money}</td>
            </tr>
        </#if>
    </#list>
</table>
```





3、输出：

姓名为“小强”则字体颜色显示为红色。

![1539947776259](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/1539947776259.png)



##### 运算符

**1、算数运算符**

FreeMarker表达式中完全支持算术运算,FreeMarker支持的算术运算符包括:

- 加法： `+`
- 减法： `-`
- 乘法： `*`
- 除法： `/`
- 求模 (求余)： `%`



模板代码

```html
<b>算数运算符</b>
<br/><br/>
    100+5 运算：  ${100 + 5 }<br/>
    100 - 5 * 5运算：${100 - 5 * 5}<br/>
    5 / 2运算：${5 / 2}<br/>
    12 % 10运算：${12 % 10}<br/>
<hr>
```

除了 + 运算以外，其他的运算只能和 number 数字类型的计算。







**2、比较运算符**

- **`=`**或者**`==`**:判断两个值是否相等. 
- **`!=`**:判断两个值是否不等. 
- **`>`**或者**`gt`**:判断左边值是否大于右边值 
- **`>=`**或者**`gte`**:判断左边值是否大于等于右边值 
- **`<`**或者**`lt`**:判断左边值是否小于右边值 
- **`<=`**或者**`lte`**:判断左边值是否小于等于右边值 



= 和 == 模板代码

```html
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>Hello World!</title>
</head>
<body>

    <b>比较运算符</b>
    <br/>
    <br/>

    <dl>
        <dt> =/== 和 != 比较：</dt>
        <dd>
            <#if "xiaoming" == "xiaoming">
                字符串的比较 "xiaoming" == "xiaoming"
            </#if>
        </dd>
        <dd>
            <#if 10 != 100>
                数值的比较 10 != 100
            </#if>
        </dd>
    </dl>



    <dl>
        <dt>其他比较</dt>
        <dd>
            <#if 10 gt 5 >
                形式一：使用特殊字符比较数值 10 gt 5
            </#if>
        </dd>
        <dd>
            <#-- 日期的比较需要通过?date将属性转为data类型才能进行比较 -->
            <#if (date1?date >= date2?date)>
                形式二：使用括号形式比较时间 date1?date >= date2?date
            </#if>
        </dd>
    </dl>

    <br/>
<hr>
</body>
</html>
```

Controller 的 数据模型代码

```java
@GetMapping("operation")
public String testOperation(Model model) {
    //构建 Date 数据
    Date now = new Date();
    model.addAttribute("date1", now);
    model.addAttribute("date2", now);
    
    return "03-operation";
}
```



**比较运算符注意**

- **`=`**和**`!=`**可以用于字符串、数值和日期来比较是否相等
- **`=`**和**`!=`**两边必须是相同类型的值,否则会产生错误
- 字符串 **`"x"`** 、**`"x "`** 、**`"X"`**比较是不等的.因为FreeMarker是精确比较
- 其它的运行符可以作用于数字和日期,但不能作用于字符串
- 使用**`gt`**等字母运算符代替**`>`**会有更好的效果,因为 FreeMarker会把**`>`**解释成FTL标签的结束字符
- 可以使用括号来避免这种情况,如:**`<#if (x>y)>`**





**3、逻辑运算符**

- 逻辑与:&& 
- 逻辑或:|| 
- 逻辑非:! 

逻辑运算符只能作用于布尔值,否则将产生错误 。



模板代码

```html
<b>逻辑运算符</b>
    <br/>
    <br/>
    <#if (10 lt 12 )&&( 10  gt  5 )  >
        (10 lt 12 )&&( 10  gt  5 )  显示为 true
    </#if>
    <br/>
    <br/>
    <#if !false>
        false 取反为true
    </#if>
<hr>
```



##### 空值处理

**1、判断某变量是否存在使用 “??”**

用法为:variable??,如果该变量存在,返回true,否则返回false 

例：为防止stus为空报错可以加上判断如下：

```velocity
    <#if stus??>
    <#list stus as stu>
    	......
    </#list>
    </#if>
```



**2、缺失变量默认值使用 “!”**

- 使用!要以指定一个默认值，当变量为空时显示默认值

  例：  ${name!''}表示如果name为空显示空字符串。



- 如果是嵌套对象则建议使用（）括起来

  例： ${(stu.bestFriend.name)!''}表示，如果stu或bestFriend或name为空默认显示空字符串。





##### 内建函数

内建函数语法格式： **`变量+?+函数名称`**  

**1、和到某个集合的大小**

**`${集合名?size}`**



**2、日期格式化**

显示年月日: **`${today?date}`** 
显示时分秒：**`${today?time}`**   
显示日期+时间：**`${today?datetime}`**   
自定义格式化：  **`${today?string("yyyy年MM月")}`**



**3、内建函数`c`**

model.addAttribute("point", 102920122);

point是数字型，使用${point}会显示这个数字的值，每三位使用逗号分隔。

如果不想显示为每三位分隔的数字，可以使用c函数将数字型转成字符串输出

**`${point?c}`**



**4、将json字符串转成对象**

一个例子：

其中用到了 assign标签，assign的作用是定义一个变量。

```velocity
<#assign text="{'bank':'工商银行','account':'10101920201920212'}" />
<#assign data=text?eval />
开户行：${data.bank}  账号：${data.account}
```



模板代码：

````HTML
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>inner Function</title>
</head>
<body>

    <b>获得集合大小</b><br>

    集合大小：
    <hr>


    <b>获得日期</b><br>

    显示年月日:      <br>

    显示时分秒：<br>

    显示日期+时间：<br>

    自定义格式化：  <br>

    <hr>

    <b>内建函数C</b><br>
    没有C函数显示的数值： <br>

    有C函数显示的数值：

    <hr>

    <b>声明变量assign</b><br>


<hr>
</body>
</html>
````





内建函数模板页面：

```velocity
<!DOCTYPE html>
<html>
<head>
    <meta charset="utf-8">
    <title>inner Function</title>
</head>
<body>

    <b>获得集合大小</b><br>

    集合大小：${stus?size}
    <hr>


    <b>获得日期</b><br>

    显示年月日: ${today?date}       <br>

    显示时分秒：${today?time}<br>

    显示日期+时间：${today?datetime}<br>

    自定义格式化：  ${today?string("yyyy年MM月")}<br>

    <hr>

    <b>内建函数C</b><br>
    没有C函数显示的数值：${point} <br>

    有C函数显示的数值：${point?c}

    <hr>

    <b>声明变量assign</b><br>
    <#assign text="{'bank':'工商银行','account':'10101920201920212'}" />
    <#assign data=text?eval />
    开户行：${data.bank}  账号：${data.account}

<hr>
</body>
</html>
```

内建函数Controller数据模型：

```java
@GetMapping("innerFunc")
public String testInnerFunc(Model model) {
    //1.1 小强对象模型数据
    Student stu1 = new Student();
    stu1.setName("小强");
    stu1.setAge(18);
    stu1.setMoney(1000.86f);
    stu1.setBirthday(new Date());
    //1.2 小红对象模型数据
    Student stu2 = new Student();
    stu2.setName("小红");
    stu2.setMoney(200.1f);
    stu2.setAge(19);
    //1.3 将两个对象模型数据存放到List集合中
    List<Student> stus = new ArrayList<>();
    stus.add(stu1);
    stus.add(stu2);
    model.addAttribute("stus", stus);
    // 2.1 添加日期
    Date date = new Date();
    model.addAttribute("today", date);
    // 3.1 添加数值
    model.addAttribute("point", 102920122);
    return "04-innerFunc";
}
```







#### 静态化测试

之前的测试都是SpringMVC将Freemarker作为视图解析器（ViewReporter）来集成到项目中，工作中，有的时候需要使用Freemarker原生Api来生成静态内容，下面一起来学习下原生Api生成文本文件。

##### 需求分析

使用freemarker原生Api将页面生成html文件，本节测试html文件生成的方法：

![image-20210422163843108](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20210422163843108.png)

##### 静态化测试 

根据模板文件生成html文件

①：修改application.yml文件，添加以下模板存放位置的配置信息，完整配置如下：

```yaml
server:
  port: 8881 #服务端口
spring:
  application:
    name: freemarker-demo #指定服务名
  freemarker:
    cache: false  #关闭模板缓存，方便测试
    settings:
      template_update_delay: 0 #检查模板更新延迟时间，设置为0表示立即检查，如果时间大于0会有缓存不方便进行模板测试
    suffix: .ftl               #指定Freemarker模板文件的后缀名
    template-loader-path: classpath:/templates   #模板存放位置
```

②：在test下创建测试类

```java
package com.heima.freemarker.test;


import com.heima.freemarker.FreemarkerDemoApplication;
import com.heima.freemarker.entity.Student;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

@SpringBootTest(classes = FreemarkerDemoApplication.class)
@RunWith(SpringRunner.class)
public class FreemarkerTest {

    @Autowired
    private Configuration configuration;

    @Test
    public void test() throws IOException, TemplateException {
        //freemarker的模板对象，获取模板
        Template template = configuration.getTemplate("02-list.ftl");
        Map params = getData();
        //合成
        //第一个参数 数据模型
        //第二个参数  输出流
        template.process(params, new FileWriter("d:/list.html"));
    }

    private Map getData() {
        Map<String, Object> map = new HashMap<>();

        //小强对象模型数据
        Student stu1 = new Student();
        stu1.setName("小强");
        stu1.setAge(18);
        stu1.setMoney(1000.86f);
        stu1.setBirthday(new Date());

        //小红对象模型数据
        Student stu2 = new Student();
        stu2.setName("小红");
        stu2.setMoney(200.1f);
        stu2.setAge(19);

        //将两个对象模型数据存放到List集合中
        List<Student> stus = new ArrayList<>();
        stus.add(stu1);
        stus.add(stu2);

        //向map中存放List集合数据
        map.put("stus", stus);


        //创建Map数据
        HashMap<String, Student> stuMap = new HashMap<>();
        stuMap.put("stu1", stu1);
        stuMap.put("stu2", stu2);
        //向map中存放Map数据
        map.put("stuMap", stuMap);

        //返回Map
        return map;
    }
}
```

### MinIO

#### 简介

MinIO基于Apache License v2.0开源协议的对象存储服务，可以做为云存储的解决方案用来保存海量的图片，视频，文档。由于采用Golang实现，服务端可以工作在Windows,Linux, OS X和FreeBSD上。配置简单，基本是复制可执行程序，单行命令可以运行起来。

MinIO兼容亚马逊S3云存储服务接口，非常适合于存储大容量非结构化的数据，例如图片、视频、日志文件、备份数据和容器/虚拟机镜像等，而一个对象文件可以是任意大小，从几kb到最大5T不等。

**S3 （ Simple Storage Service简单存储服务）**

基本概念

- bucket – 类比于文件系统的目录
- Object – 类比文件系统的文件
- Keys – 类比文件名

官网文档：http://docs.minio.org.cn/docs/

> 数据特点

- 数据保护

  Minio使用Minio Erasure Code（纠删码）来防止硬件故障。即便损坏一半以上的driver，但是仍然可以从中恢复。

- 高性能

   作为高性能对象存储，在标准硬件条件下它能达到55GB/s的读、35GB/s的写速率

- 可扩容

  不同MinIO集群可以组成联邦，并形成一个全局的命名空间，并跨越多个数据中心

- SDK支持

  基于Minio轻量的特点，它得到类似Java、Python或Go等语言的sdk支持

- 有操作页面

  面向用户友好的简单操作界面，非常方便的管理Bucket及里面的文件资源

- 功能简单

  这一设计原则让MinIO不容易出错、更快启动

- 丰富的API

  支持文件资源的分享连接及分享链接的过期策略、存储桶操作、文件列表访问及文件上传下载的基本功能等。

- 文件变化主动通知

  存储桶（Bucket）如果发生改变,比如上传对象和删除对象，可以使用存储桶事件通知机制进行监控，并通过以下方式发布出去:AMQP、MQTT、Elasticsearch、Redis、NATS、MySQL、Kafka、Webhooks等。

#### 快速入门

> 容器方式启动MinIO

```shell
docker run -p 9000:9000 --name minio -d --restart=always -e "MINIO_ACCESS_KEY=minio" -e "MINIO_SECRET_KEY=minio123" -v /mydata/minio/data:/data -v /mydata/minio/config:/root/.minio minio/minio server /data
```

假设我们的服务器地址为http://192.168.200.130:9000，我们在地址栏输入：http://http://192.168.200.130:9000/ 即可进入登录界面。

新建一个桶，为后期做准备

![image-20230214194640754](../../../../../Library/Application Support/typora-user-images/image-20230214194640754.png)

> 使用MinIO

1、导入pom文件

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <parent>
        <artifactId>heima-leadnews-test</artifactId>
        <groupId>com.heima</groupId>
        <version>1.0-SNAPSHOT</version>
    </parent>
    <modelVersion>4.0.0</modelVersion>

    <artifactId>minio-demo</artifactId>

    <properties>
        <maven.compiler.source>8</maven.compiler.source>
        <maven.compiler.target>8</maven.compiler.target>
    </properties>

    <dependencies>

        <dependency>
            <groupId>io.minio</groupId>
            <artifactId>minio</artifactId>
            <version>7.1.0</version>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
        </dependency>
    </dependencies>

</project>
```

2、引导类

```java
package com.heima.minio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class MinIOApplication {

    public static void main(String[] args) {
        SpringApplication.run(MinIOApplication.class,args);
    }
}
```

3、创建测试类，上传html文件

```java
package com.heima.minio.test;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;

import java.io.FileInputStream;

public class MinIOTest {


    public static void main(String[] args) {

        FileInputStream fileInputStream = null;
        try {

            fileInputStream =  new FileInputStream("D:\\list.html");;

            //1.创建minio链接客户端
            MinioClient minioClient = MinioClient.builder().credentials("minio", "minio123").endpoint("http://192.168.200.130:9000").build();
            //2.上传
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object("list.html")//文件名
                    .contentType("text/html")//文件类型
                    .bucket("leadnews")//桶名词  与minio创建的名词一致
                    .stream(fileInputStream, fileInputStream.available(), -1) //文件流
                    .build();
            minioClient.putObject(putObjectArgs);

            System.out.println("http://192.168.200.130:9000/leadnews/ak47.jpg");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
```

#### 封装MinIO

> 为什么要封装MinIO?

为了其他微服务更方便的上传文件，我们直接把MinIO封装成一个微服务模块，这样其他微服务只要引入该模块就可以方便的上传图片或者文件可以直接去调用该微服务的API

> 如何封装

##### 创建模块

![image-20230214204630688](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230214204630688.png)

##### 导入依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-autoconfigure</artifactId>
    </dependency>
    <dependency>
        <groupId>io.minio</groupId>
        <artifactId>minio</artifactId>
        <version>7.1.0</version>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-configuration-processor</artifactId>
        <optional>true</optional>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-actuator</artifactId>
    </dependency>
</dependencies>
```

##### **配置类**

MinIOConfigProperties

```java
package com.heima.file.config;


import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.io.Serializable;

@Data
@ConfigurationProperties(prefix = "minio")  // 文件上传 配置前缀file.oss
public class MinIOConfigProperties implements Serializable {

    private String accessKey;
    private String secretKey;
    private String bucket;
    private String endpoint;
    private String readPath;
}
```

MinIOConfig

```java
package com.heima.file.config;

import com.heima.file.service.FileStorageService;
import io.minio.MinioClient;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Data
@Configuration
@EnableConfigurationProperties({MinIOConfigProperties.class})
//当引入FileStorageService接口时
@ConditionalOnClass(FileStorageService.class)
public class MinIOConfig {

   @Autowired
   private MinIOConfigProperties minIOConfigProperties;

    @Bean
    public MinioClient buildMinioClient(){
        return MinioClient
                .builder()
                .credentials(minIOConfigProperties.getAccessKey(), minIOConfigProperties.getSecretKey())
                .endpoint(minIOConfigProperties.getEndpoint())
                .build();
    }
}
```

##### 封装操作minIO类

FileStorageService

```java
package com.heima.file.service;

import java.io.InputStream;

/**
 * @author itheima
 */
public interface FileStorageService {


    /**
     *  上传图片文件
     * @param prefix  文件前缀
     * @param filename  文件名
     * @param inputStream 文件流
     * @return  文件全路径
     */
    public String uploadImgFile(String prefix, String filename,InputStream inputStream);

    /**
     *  上传html文件
     * @param prefix  文件前缀
     * @param filename   文件名
     * @param inputStream  文件流
     * @return  文件全路径
     */
    public String uploadHtmlFile(String prefix, String filename,InputStream inputStream);

    /**
     * 删除文件
     * @param pathUrl  文件全路径
     */
    public void delete(String pathUrl);

    /**
     * 下载文件
     * @param pathUrl  文件全路径
     * @return
     *
     */
    public byte[]  downLoadFile(String pathUrl);

}
```

MinIOFileStorageService

```java
package com.heima.file.service.impl;


import com.heima.file.config.MinIOConfig;
import com.heima.file.config.MinIOConfigProperties;
import com.heima.file.service.FileStorageService;
import io.minio.GetObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

@Slf4j
@EnableConfigurationProperties(MinIOConfigProperties.class)
@Import(MinIOConfig.class)
public class MinIOFileStorageService implements FileStorageService {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private MinIOConfigProperties minIOConfigProperties;

    private final static String separator = "/";

    /**
     * @param dirPath
     * @param filename  yyyy/mm/dd/file.jpg
     * @return
     */
    public String builderFilePath(String dirPath,String filename) {
        StringBuilder stringBuilder = new StringBuilder(50);
        if(!StringUtils.isEmpty(dirPath)){
            stringBuilder.append(dirPath).append(separator);
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        String todayStr = sdf.format(new Date());
        stringBuilder.append(todayStr).append(separator);
        stringBuilder.append(filename);
        return stringBuilder.toString();
    }

    /**
     *  上传图片文件
     * @param prefix  文件前缀
     * @param filename  文件名
     * @param inputStream 文件流
     * @return  文件全路径
     */
    @Override
    public String uploadImgFile(String prefix, String filename,InputStream inputStream) {
        String filePath = builderFilePath(prefix, filename);
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(filePath)
                    .contentType("image/jpg")
                    .bucket(minIOConfigProperties.getBucket()).stream(inputStream,inputStream.available(),-1)
                    .build();
            minioClient.putObject(putObjectArgs);
            StringBuilder urlPath = new StringBuilder(minIOConfigProperties.getReadPath());
            urlPath.append(separator+minIOConfigProperties.getBucket());
            urlPath.append(separator);
            urlPath.append(filePath);
            return urlPath.toString();
        }catch (Exception ex){
            log.error("minio put file error.",ex);
            throw new RuntimeException("上传文件失败");
        }
    }

    /**
     *  上传html文件
     * @param prefix  文件前缀
     * @param filename   文件名
     * @param inputStream  文件流
     * @return  文件全路径
     */
    @Override
    public String uploadHtmlFile(String prefix, String filename,InputStream inputStream) {
        String filePath = builderFilePath(prefix, filename);
        try {
            PutObjectArgs putObjectArgs = PutObjectArgs.builder()
                    .object(filePath)
                    .contentType("text/html")
                    .bucket(minIOConfigProperties.getBucket()).stream(inputStream,inputStream.available(),-1)
                    .build();
            minioClient.putObject(putObjectArgs);
            StringBuilder urlPath = new StringBuilder(minIOConfigProperties.getReadPath());
            urlPath.append(separator+minIOConfigProperties.getBucket());
            urlPath.append(separator);
            urlPath.append(filePath);
            return urlPath.toString();
        }catch (Exception ex){
            log.error("minio put file error.",ex);
            ex.printStackTrace();
            throw new RuntimeException("上传文件失败");
        }
    }

    /**
     * 删除文件
     * @param pathUrl  文件全路径
     */
    @Override
    public void delete(String pathUrl) {
        String key = pathUrl.replace(minIOConfigProperties.getEndpoint()+"/","");
        int index = key.indexOf(separator);
        String bucket = key.substring(0,index);
        String filePath = key.substring(index+1);
        // 删除Objects
        RemoveObjectArgs removeObjectArgs = RemoveObjectArgs.builder().bucket(bucket).object(filePath).build();
        try {
            minioClient.removeObject(removeObjectArgs);
        } catch (Exception e) {
            log.error("minio remove file error.  pathUrl:{}",pathUrl);
            e.printStackTrace();
        }
    }


    /**
     * 下载文件
     * @param pathUrl  文件全路径
     * @return  文件流
     *
     */
    @Override
    public byte[] downLoadFile(String pathUrl)  {
        String key = pathUrl.replace(minIOConfigProperties.getEndpoint()+"/","");
        int index = key.indexOf(separator);
        String bucket = key.substring(0,index);
        String filePath = key.substring(index+1);
        InputStream inputStream = null;
        try {
            inputStream = minioClient.getObject(GetObjectArgs.builder().bucket(minIOConfigProperties.getBucket()).object(filePath).build());
        } catch (Exception e) {
            log.error("minio down file error.  pathUrl:{}",pathUrl);
            e.printStackTrace();
        }

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buff = new byte[100];
        int rc = 0;
        while (true) {
            try {
                if (!((rc = inputStream.read(buff, 0, 100)) > 0)) break;
            } catch (IOException e) {
                e.printStackTrace();
            }
            byteArrayOutputStream.write(buff, 0, rc);
        }
        return byteArrayOutputStream.toByteArray();
    }
}
```

##### 对外加入自动配置

在resources中新建`META-INF/spring.factories`

```java
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.heima.file.service.impl.MinIOFileStorageService
```

##### 其他微服务使用

第一，导入heima-file-starter的依赖

第二，在微服务中添加minio所需要的配置

```yaml
minio:
  accessKey: minio
  secretKey: minio123
  bucket: leadnews
  endpoint: http://localhost:9000
  readPath: http://localhost:9000
```

第三，在对应使用的业务类中注入FileStorageService，样例如下：

```java
package com.heima.minio.test;


import com.heima.file.service.FileStorageService;
import com.heima.minio.MinioApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

@SpringBootTest(classes = MinioApplication.class)
@RunWith(SpringRunner.class)
public class MinioTest {

  @Autowired
  private FileStorageService fileStorageService;

  @Test
  public void testUpdateImgFile() {
    try {
      FileInputStream fileInputStream = new FileInputStream("/Users/xiaocai/Documents/icon/Asias.jpeg");
      String filePath = fileStorageService.uploadImgFile("", "Asias.jpeg", fileInputStream);
      System.out.println(filePath);
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    }
  }
}
```

### 文章详情

#### 需求分析

![image-20210602180753705](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20210602180753705.png)

#### 实现方案

方案一

用户某一条文章，根据文章的id去查询文章内容表，返回渲染页面

![image-20210602180824202](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20210602180824202.png)

方案二

![image-20210602180856833](../../../Java高级/09阶段九：项目实战-黑马头条(V12.5)/02-资料/黑马头条/day02-app端文章查看，静态化freemarker,分布式文件系统minIO/讲义/app端文章查看，静态化freemarker,分布式文件系统minIO.assets/image-20210602180856833.png)

#### 实现步骤

1.在artile微服务中添加MinIO和freemarker的支持，参考测试项目

2.资料中找到模板文件（article.ftl）拷贝到article微服务下

![image-20210602180931839](../../../Java高级/09阶段九：项目实战-黑马头条(V12.5)/02-资料/黑马头条/day02-app端文章查看，静态化freemarker,分布式文件系统minIO/讲义/app端文章查看，静态化freemarker,分布式文件系统minIO.assets/image-20210602180931839.png)

3.资料中找到index.js和index.css两个文件手动上传到MinIO中

![image-20210602180957787](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20210602180957787.png)

4.在文章微服务中导入依赖

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-freemarker</artifactId>
    </dependency>
    <dependency>
        <groupId>com.heima</groupId>
        <artifactId>heima-file-starter</artifactId>
        <version>1.0-SNAPSHOT</version>
    </dependency>
</dependencies>
```

5.新建ApArticleContentMapper

```java
package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.pojos.ApArticleContent;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ApArticleContentMapper extends BaseMapper<ApArticleContent> {
}
```

6.在artile微服务中新增测试类（后期新增文章的时候创建详情静态页，目前暂时手动生成）

```java
package com.heima.article.test;


import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.ArticleApplication;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleContent;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@SpringBootTest(classes = ArticleApplication.class)
@RunWith(SpringRunner.class)
public class ArticleFreemarkerTest {

    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;


    @Autowired
    private ApArticleMapper apArticleMapper;

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Test
    public void createStaticUrlTest() throws Exception {
        //1.获取文章内容
        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, 1390536764510310401L));
        if(apArticleContent != null && StringUtils.isNotBlank(apArticleContent.getContent())){
            //2.文章内容通过freemarker生成html文件
            StringWriter out = new StringWriter();
            Template template = configuration.getTemplate("article.ftl");

            Map<String, Object> params = new HashMap<>();
            params.put("content", JSONArray.parseArray(apArticleContent.getContent()));

            template.process(params, out);
            InputStream is = new ByteArrayInputStream(out.toString().getBytes());

            //3.把html文件上传到minio中
            String path = fileStorageService.uploadHtmlFile("", apArticleContent.getArticleId() + ".html", is);

            //4.修改ap_article表，保存static_url字段
            ApArticle article = new ApArticle();
            article.setId(apArticleContent.getArticleId());
            article.setStaticUrl(path);
            apArticleMapper.updateById(article);

        }
    }
}
```





## 自媒体文章发布

### 自媒体前后端搭建

#### 后台搭建

![](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230215105441142.png)



①：资料中找到heima-leadnews-wemedia.zip解压

 拷贝到heima-leadnews-service工程下，并指定子模块

 执行leadnews-wemedia.sql脚本

 添加对应的nacos配置

```yaml
spring:
  datasource:
    driver-class-name: com.mysql.jdbc.Driver
    url: jdbc:mysql://localhost:3306/leadnews_wemedia?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
    username: root
    password: root
# 设置Mapper接口所对应的XML文件位置，如果你在Mapper接口中有自定义方法，需要进行该配置
mybatis-plus:
  mapper-locations: classpath*:mapper/*.xml
  # 设置别名包扫描路径，通过该属性可以给包中的类注册别名
  type-aliases-package: com.heima.model.media.pojos
```



②：资料中找到heima-leadnews-wemedia-gateway.zip解压

 拷贝到heima-leadnews-gateway工程下，并指定子模块

 添加对应的nacos配置

```yaml
spring:
  cloud:
    gateway:
      globalcors:
        cors-configurations:
          '[/**]': # 匹配所有请求
            allowedOrigins: "*" #跨域处理 允许所有的域
            allowedMethods: # 支持的方法
              - GET
              - POST
              - PUT
              - DELETE
      routes:
        # 平台管理
        - id: wemedia
          uri: lb://leadnews-wemedia
          predicates:
            - Path=/wemedia/**
          filters:
            - StripPrefix= 1
```



③：在资料中找到类文件夹

 拷贝wemedia文件夹到heima-leadnews-model模块下的com.heima.model

#### 前台搭建

![image-20210426110913007](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20210426110913007.png)

通过nginx的虚拟主机功能，使用同一个nginx访问多个项目



搭建步骤：

①：资料中找到wemedia-web.zip解压

②：在nginx中leadnews.conf目录中新增heima-leadnews-wemedia.conf文件

- 网关地址修改（localhost:51602）

- 前端项目目录修改（wemedia-web解压的目录）

- 访问端口修改(8802)

```javascript
upstream  heima-wemedia-gateway{
    server localhost:51602;
}

server {
	listen 8802;
	location / {
		root D:/workspace/wemedia-web/;
		index index.html;
	}
	
	location ~/wemedia/MEDIA/(.*) {
		proxy_pass http://heima-wemedia-gateway/$1;
		proxy_set_header HOST $host;  # 不改变源请求头的值
		proxy_pass_request_body on;  #开启获取请求体
		proxy_pass_request_headers on;  #开启获取请求头
		proxy_set_header X-Real-IP $remote_addr;   # 记录真实发出请求的客户端IP
		proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;  #记录代理信息
	}
}
```

③：启动nginx，启动自媒体微服务和对应网关

④：联调测试登录功能

![image-20210426111329136](../../../Java高级/09阶段九：项目实战-黑马头条(V12.5)/02-资料/黑马头条/day03-自媒体文章发布/讲义/自媒体文章发布.assets/image-20210426111329136.png)

### 自媒体素材管理

#### 素材上传

![image-20230215110003159](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230215110003159.png)

图片上传的页面，首先是展示素材信息，可以点击图片上传，弹窗后可以上传图片

##### 素材管理-图片上传-表结构

媒体图文素材信息表wm_material

![image-20230215110137003](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230215110137003.png)





生成实体类

...

##### 实现思路

①：前端发送上传图片请求，类型为MultipartFile

②：网关进行token解析后，把解析后的用户信息存储到header中

③：自媒体微服务使用拦截器获取到header中的的用户信息，并放入到threadlocal中

④：先把图片上传到minIO中，获取到图片请求的路径

⑤：把用户id和图片上的路径保存到素材表中

##### 实现代码

```java
package com.heima.wemedia.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.utils.thread.WmThreadLocalUtil;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@Transactional
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Autowired
    private FileStorageService fileStorageService;


    /**
     * 图片上传
     * @param multipartFile
     * @return
     */
    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {

        //1.检查参数
        if(multipartFile == null || multipartFile.getSize() == 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.上传图片到minIO中
        String fileName = UUID.randomUUID().toString().replace("-", "");
        //aa.jpg
        String originalFilename = multipartFile.getOriginalFilename();
        String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileId = null;
        try {
            fileId = fileStorageService.uploadImgFile("", fileName + postfix, multipartFile.getInputStream());
            log.info("上传图片到MinIO中，fileId:{}",fileId);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("WmMaterialServiceImpl-上传文件失败");
        }

        //3.保存到数据库中
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(WmThreadLocalUtil.getUser().getId());
        wmMaterial.setUrl(fileId);
        wmMaterial.setIsCollection((short)0);
        wmMaterial.setType((short)0);
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);

        //4.返回结果

        return ResponseResult.okResult(wmMaterial);
    }

}
```

#### 素材列表查询

多条件分页查询,这里不在展示



### 自媒体文章管理

#### 查询所有频道

就是普通的查询，代码不在展示

#### 查询自媒体文章

![image-20230215110958250](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230215110958250.png)

这里也是多条件分页查询，不再展示

#### 文章发布

![image-20230215111242087](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230215111242087.png)



##### 表结构分析

![image-20230215111400711](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230215111400711.png)

##### 逻辑分析

把需求的逻辑分析清楚，写代码是很简单的

首先，和发布文章相关的表有三张：

- 文章表（news）
- 素材表（material）
- 文章和素材表（news_material）

第二，发布文章的逻辑：

- 数据库不存在文章id

  - 新增文章到数据库（news）
  - 关联文章和图片的关系（news_material）
  - 关联封面图片和素材的关系

  - 发布文章结束

- 看看数据库有没有该文章id，有的话代表文章已存在，属于是对文章的修改
  - 删除文章与素材的关系(news_material)
  - 修改数据库（news）
  - 重新关联文章与图片关系(news_material)
  - 重新关联封面图片和素材的关系
  - 发布文章结束

第三，流程图展示：

![image-20230215112926523](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230215112926523.png)



##### 代码实现

> 1、主方法，用来判断是发布还是保存草稿

0.条件判断

```
  if(dto == null || dto.getContent() == null){
    return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
  }
```

1.保存或修改文章

```java
WmNews wmNews = new WmNews();
//属性拷贝 属性名词和类型相同才能拷贝
BeanUtils.copyProperties(dto,wmNews);
//封面图片  list---> string
if(dto.getImages() != null && dto.getImages().size() > 0){
  //[1dddfsd.jpg,sdlfjldk.jpg]-->   1dddfsd.jpg,sdlfjldk.jpg
  String imageStr = StringUtils.join(dto.getImages(), ",");
  wmNews.setImages(imageStr);
}
//如果当前封面类型为自动 -1
if(dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
  wmNews.setType(null);
}

saveOrUpdateWmNews(wmNews);
```

2.判断是否为草稿  如果为草稿结束当前方法

```java
if(dto.getStatus().equals(WmNews.Status.NORMAL.getCode())){
    return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
}
```

3.不是草稿，保存文章内容图片与素材的关系

下图为前端发布文章传来的内容，可以看到是一个json数组

![image-20230215115935314](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230215115935314.png)

那么后端该如何去解析这些数据，获得图片相关的的value呢？

- 把前端传来的数据当成一个Map类型的List集合
- 对Map集合进行遍历，如果type为image那么就取出value的值
- 把收集到的图片url加入新建的集合materials中返回

```java
private List<String> ectractUrlInfo(String content) {
  List<String> materials = new ArrayList<>();

  List<Map> maps = JSON.parseArray(content, Map.class);
  for (Map map : maps) {
    if(map.get("type").equals("image")){
      String imgUrl = (String) map.get("value");
      materials.add(imgUrl);
    }
  }

  return materials;
}
```

> 保存素材和文章的关系

```java
private void saveRelativeInfo(List<String> materials, Integer newsId, Short type) {
    if(materials != null && !materials.isEmpty()){
        //通过图片的url查询素材的id
        List<WmMaterial> dbMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, materials));

        //判断素材是否有效
        if(dbMaterials==null || dbMaterials.size() == 0){
            //手动抛出异常   第一个功能：能够提示调用者素材失效了，第二个功能，进行数据的回滚
            throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
        }

        if(materials.size() != dbMaterials.size()){
            throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
        }

        List<Integer> idList = dbMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());

        //批量保存
        wmNewsMaterialMapper.saveRelations(idList,newsId,type);
    }
}

    <insert id="saveRelations">
        insert into wm_news_material (material_id,news_id,type,ord)
        values
        <foreach collection="materialIds" index="ord" item="mid" separator=",">
            (#{mid},#{newsId},#{type},#{ord})
        </foreach>
    </insert>
```

4.不是草稿，保存文章封面图片与素材的关系，如果当前布局是自动，需要匹配封面图片

```java
 private void saveRelativeInfoForCover(WmNewsDto dto, WmNews wmNews, List<String> materials) {

        List<String> images = dto.getImages();

        //如果当前封面类型为自动，则设置封面类型的数据
        if(dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            //多图
            if(materials.size() >= 3){
                wmNews.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = materials.stream().limit(3).collect(Collectors.toList());
            }else if(materials.size() >= 1 && materials.size() < 3){
                //单图
                wmNews.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = materials.stream().limit(1).collect(Collectors.toList());
            }else {
                //无图
                wmNews.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }

            //修改文章
            if(images != null && images.size() > 0){
                wmNews.setImages(StringUtils.join(images,","));
            }
            updateById(wmNews);
        }
        //第二个功能：保存封面图片与素材的关系
        if(images != null && images.size() > 0){
            saveRelativeInfo(images,wmNews.getId(),WemediaConstants.WM_COVER_REFERENCE);
        }

    }
```

看上去看多，把功能理清楚了，其实也是CRUD



## 自媒体文章审核

### 自媒体文章自动审核流程

![image-20230215223041634](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230215223041634.png)

> 1 自媒体端发布文章后，开始审核文章
>
> 2 审核的主要是审核文章的内容（文本内容和图片）
>
> 3 借助第三方提供的接口审核文本
>
> 4 借助第三方提供的接口审核图片，由于图片存储到minIO中，需要先下载才能审核
>
> 5 如果审核失败，则需要修改自媒体文章的状态，status:2  审核失败    status:3  转到人工审核
>
> 6 如果审核成功，则需要在文章微服务中创建app端需要的文章

### 内容安全第三方接口

#### 概述

阿里云不好使，这里使用百度云的来进行测试

百度云搜索内容识别即可使用

https://ai.baidu.com/tech/textcensoring

![image-20230216084625919](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230216084625919.png)

> 图像测试

![image-20230216085200469](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230216085200469.png)

> 文本测试

![image-20230216085449105](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230216085449105.png)

#### 代码

百度云所需要的key、secret、appid均写在nacos配置中心

> 图片检测

```java
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

        resultMap.put("conclusion", conclusion);
        resultMap.put("msg", msg);
        return resultMap;

    }

}
```

> 内容检测

```java
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

        resultMap.put("conclusion", "合格");
        resultMap.put("msg", msg);
        return resultMap;
    }
}
```

### App端文章保存接口

#### 表结构说明

![image-20230216154244737](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230216154244737.png)



#### 分布式id

> 分布式系统下，id重复的问题

随着业务的增长，文章表可能要占用很大的物理存储空间，为了解决该问题，后期使用数据库分片技术。将一个数据库进行拆分，通过数据库中间件连接。如果数据库中该表选用ID自增策略，则可能产生重复的ID，此时应该使用分布式ID生成策略来生成ID。

![image-20230216154342715](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230216154342715.png)

> 如何解决分布式系统下，id重复的问题？

![image-20230216155258782](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230216155258782.png)

> 雪花算法

`snowflake`是Twitter开源的分布式ID生成算法，结果是一个long型的ID。其核心思想是：**使用41bit作为毫秒数**，**10bit作为机器的ID（5个bit是数2据中心，5个bit的机器ID）**，12bit作为毫秒内的流水号（**意味着每个节点在每`毫秒`可以产生(2的12次方) 4096 个 ID**），最后还有一个符号位，永远是0

![image-20230216154405241](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230216154405241.png)

文章端相关的表都使用雪花算法生成id,包括ap_article、 ap_article_config、 ap_article_content

mybatis-plus已经集成了雪花算法，完成以下两步即可在项目中集成雪花算法

第一：在实体类中的id上加入如下配置，指定类型为id_worker

```java
@TableId(value = "id",type = IdType.ID_WORKER)
private Long id;
```

第二：在application.yml文件中配置数据中心id和机器id

```yaml
mybatis-plus:
  mapper-locations: classpath*:mapper/*.xml
  # 设置别名包扫描路径，通过该属性可以给包中的类注册别名
  type-aliases-package: com.heima.model.article.pojos
  global-config:
    datacenter-id: 1
    workerId: 1
```

datacenter-id:数据中心id(取值范围：0-31)

workerId:机器id(取值范围：0-31)

#### 思路分析

在文章审核成功以后需要在app的article库中新增文章数据

1.保存文章信息 ap_article

2.保存文章配置信息 ap_article_config

3.保存文章内容 ap_article_content

实现思路：

![image-20230216155433859](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230216155433859.png)

#### feign接口

> 如何实现远程调用？

**dubbo或者springcloud**

|          | **说明**             |
| -------- | -------------------- |
| 接口路径 | /api/v1/article/save |
| 请求方式 | POST                 |
| 参数     | ArticleDto           |
| 响应结果 | ResponseResult       |

ArticleDto

```java
package com.heima.model.article.dtos;

import com.heima.model.article.pojos.ApArticle;
import lombok.Data;

@Data
public class ArticleDto  extends ApArticle {
    /**
     * 文章内容
     */
    private String content;
}
```

成功：

```json
{
  "code": 200,
  "errorMessage" : "操作成功",
  "data":"1302864436297442242"
 }
```

失败：

```json
{
  "code":501,
  "errorMessage":"参数失效",
 }
```

```json
{
  "code":501,
  "errorMessage":"文章没有找到",
 }
```

功能实现：



①：在heima-leadnews-feign-api中新增接口

第一：线导入feign的依赖

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-openfeign</artifactId>
</dependency>
```

第二：定义文章端的接口

```json
package com.heima.apis.article;

import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;


@FeignClient(value = "leadnews-article")
public interface IArticleClient {

    @PostMapping("/api/v1/article/save")
    public ResponseResult saveArticle(@RequestBody ArticleDto dto) ;
}
```

②：在heima-leadnews-article中实现该方法

```java
package com.heima.article.feign;

import com.heima.apis.article.IArticleClient;
import com.heima.article.service.ApArticleService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class ArticleClient implements IArticleClient {

    @Autowired
    private ApArticleService apArticleService;

    @Override
    @PostMapping("/api/v1/article/save")
    public ResponseResult saveArticle(@RequestBody ArticleDto dto) {
        return apArticleService.saveArticle(dto);
    }

}
```

③：拷贝mapper

在资料文件夹中拷贝ApArticleConfigMapper类到mapper文件夹中

同时，修改ApArticleConfig类，添加如下构造函数

```java
package com.heima.model.article.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * APP已发布文章配置表
 * </p>
 *
 * @author itheima
 */

@Data
@NoArgsConstructor
@TableName("ap_article_config")
public class ApArticleConfig implements Serializable {


    public ApArticleConfig(Long articleId){
        this.articleId = articleId;
        this.isComment = true;
        this.isForward = true;
        this.isDelete = false;
        this.isDown = false;
    }

    @TableId(value = "id",type = IdType.ID_WORKER)
    private Long id;

    /**
     * 文章id
     */
    @TableField("article_id")
    private Long articleId;

    /**
     * 是否可评论
     * true: 可以评论   1
     * false: 不可评论  0
     */
    @TableField("is_comment")
    private Boolean isComment;

    /**
     * 是否转发
     * true: 可以转发   1
     * false: 不可转发  0
     */
    @TableField("is_forward")
    private Boolean isForward;

    /**
     * 是否下架
     * true: 下架   1
     * false: 没有下架  0
     */
    @TableField("is_down")
    private Boolean isDown;

    /**
     * 是否已删除
     * true: 删除   1
     * false: 没有删除  0
     */
    @TableField("is_delete")
    private Boolean isDelete;
}
```

④：在ApArticleService中新增方法

```java
/**
     * 保存app端相关文章
     * @param dto
     * @return
     */
ResponseResult saveArticle(ArticleDto dto) ;
```

实现类：

```java
@Autowired
private ApArticleConfigMapper apArticleConfigMapper;

@Autowired
private ApArticleContentMapper apArticleContentMapper;

/**
     * 保存app端相关文章
     * @param dto
     * @return
     */
@Override
public ResponseResult saveArticle(ArticleDto dto) {
    //1.检查参数
    if(dto == null){
        return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
    }

    ApArticle apArticle = new ApArticle();
    BeanUtils.copyProperties(dto,apArticle);

    //2.判断是否存在id
    if(dto.getId() == null){
        //2.1 不存在id  保存  文章  文章配置  文章内容

        //保存文章
        save(apArticle);

        //保存配置
        ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
        apArticleConfigMapper.insert(apArticleConfig);

        //保存 文章内容
        ApArticleContent apArticleContent = new ApArticleContent();
        apArticleContent.setArticleId(apArticle.getId());
        apArticleContent.setContent(dto.getContent());
        apArticleContentMapper.insert(apArticleContent);

    }else {
        //2.2 存在id   修改  文章  文章内容

        //修改  文章
        updateById(apArticle);

        //修改文章内容
        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, dto.getId()));
        apArticleContent.setContent(dto.getContent());
        apArticleContentMapper.updateById(apArticleContent);
    }


    //3.结果返回  文章的id
    return ResponseResult.okResult(apArticle.getId());
}
```

⑤：测试

编写junit单元测试，或使用postman进行测试

```json
{
	"id":1390209114747047938,
    "title":"黑马头条项目背景22222222222222",
    "authoId":1102,
    "layout":1,
    "labels":"黑马头条",
    "publishTime":"2028-03-14T11:35:49.000Z",
    "images": "http://192.168.200.130:9000/leadnews/2021/04/26/5ddbdb5c68094ce393b08a47860da275.jpg",
    "content":"22222222222222222黑马头条项目背景,黑马头条项目背景,黑马头条项目背景,黑马头条项目背景，黑马头条项目背景"
}
```

> 测试

![image-20230216170728700](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230216170728700.png)

### 自媒体文章自动审核功能

#### 表结构说明

wm_news 自媒体文章表

![image-20230216171123252](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230216171123252.png)

#### 实现

![image-20230216171141701](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230216171141701.png)

在heima-leadnews-wemedia中的service新增接口

```java
package com.heima.wemedia.service;

public interface WmNewsAutoScanService {

    /**
     * 自媒体文章审核
     * @param id  自媒体文章id
     */
    public void autoScanWmNews(Integer id);
}
```

实现类

这里我对源代码做了改进，因为我的阿里云内容识别不能用~

```java
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

    /**
     * 自媒体文章审核
     *
     * @param id 自媒体文章id
     */
    @Override
    public void autoScanWmNews(Integer id) {
//        1、查询自媒体文章
        WmNews wmNews = wmNewsMapper.selectById(id);
        if (wmNews == null) {
            throw new RuntimeException("WmNewsAutoScanServiceImpl-文章不存在");
        }

//        从内容中提取纯文本内容和图片
        if (wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())) {  //SUBMIT=1=待审核
            Map<String, Object> textAndImages = handleTextAndImages(wmNews);
//         2、审核文本内容，百度云接口
            boolean textCheck = handleScanText(wmNews, textAndImages);
            if (!textCheck) return;

//        3、审核图片的内容，百度云接口
            boolean imgCheck = handleScanImg(textAndImages, wmNews);
            if (!imgCheck) return;

//        4、审核成功，保存app端的相关数据
            ResponseResult responseResult = saveAppArticle(wmNews);
            if(!responseResult.getCode().equals(200)){
                throw new RuntimeException("WmNewsAutoScanServiceImpl-文章审核，保存app端相关文章数据失败");
            }

            //回填article_id
            wmNews.setArticleId((Long) responseResult.getData());
            updateWmnews(wmNews,(short) 9,"审核成功");

        }


    }

    private ResponseResult saveAppArticle(WmNews wmNews) {
        ArticleDto dto = new ArticleDto();
        BeanUtils.copyProperties(wmNews,dto);
        dto.setLayout(wmNews.getType());
        WmChannel wmChannel = wmChannelMapper.selectById(dto.getChannelId());

//            频道
        if (wmChannel != null) {
            dto.setChannelName(wmChannel.getName());
        }

//            作者
        dto.setAuthorId(wmNews.getUserId().longValue());
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if(wmUser != null){
            dto.setAuthorName(wmUser.getName());
        }

        //设置文章id
        if(wmNews.getArticleId() != null){
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

```









#### 单元测试

```java
@Autowired
private WmNewsAutoScanService wmNewsAutoScanService;

@Test
public void autoScanWmNews() {

    wmNewsAutoScanService.autoScanWmNews(6232);
}
```

#### feign远程接口调用方式

![image-20230216183412911](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230216183412911.png)

在heima-leadnews-wemedia服务中已经依赖了heima-leadnews-feign-apis工程，只需要在自媒体的引导类中开启feign的远程调用即可

注解为：`@EnableFeignClients(basePackages = "com.heima.apis")` 需要指向apis这个包

![image-20230216183449081](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230216183449081.png)

#### 服务降级处理

![image-20230216183513667](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230216183513667.png)

- 服务降级是服务自我保护的一种方式，或者保护下游服务的一种方式，用于确保服务不会受请求突增影响变得不可用，确保服务不会崩溃
- 服务降级虽然会导致请求失败，但是不会导致阻塞。

实现步骤：

①：在heima-leadnews-feign-api编写降级逻辑

```java
package com.heima.apis.article.fallback;

import com.heima.apis.article.IArticleClient;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import org.springframework.stereotype.Component;

/**
 * feign失败配置
 * @author itheima
 */
@Component
public class IArticleClientFallback implements IArticleClient {
    @Override
    public ResponseResult saveArticle(ArticleDto dto)  {
        return ResponseResult.errorResult(AppHttpCodeEnum.SERVER_ERROR,"获取数据失败");
    }
}
```

在自媒体微服务中添加类，扫描降级代码类的包

```java
package com.heima.wemedia.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan("com.heima.apis.article.fallback")
public class InitConfig {
}
```

②：远程接口中指向降级代码

```java
package com.heima.apis.article;

import com.heima.apis.article.fallback.IArticleClientFallback;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "leadnews-article",fallback = IArticleClientFallback.class)
public interface IArticleClient {

    @PostMapping("/api/v1/article/save")
    public ResponseResult saveArticle(@RequestBody ArticleDto dto);
}
```

③：客户端开启降级heima-leadnews-wemedia

在wemedia的nacos配置中心里添加如下内容，开启服务降级，也可以指定服务响应的超时的时间

```yaml
feign:
  # 开启feign对hystrix熔断降级的支持
  hystrix:
    enabled: true
  # 修改调用超时时间
  client:
    config:
      default:
        connectTimeout: 2000
        readTimeout: 2000
```

④：测试

在ApArticleServiceImpl类中saveArticle方法添加代码

```java
try {
    Thread.sleep(3000);
} catch (InterruptedException e) {
    e.printStackTrace();
}
```

在自媒体端进行审核测试，会出现服务降级的现象

测试结果如下：

成功触发了Hystrix实现了服务降级

![image-20230216185112500](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20230216185112500.png)

### 发布文章提交审核集成

#### 同步调用与异步调用

同步：发起调用的时候必须等待结果返回才能继续执行其他代码

异步：发起调用之后，不需要等待返回结果，可以继续执行下面的代码

项目这里其实就可以把**文章审核通过异步的形式去进行调用**

#### Springboot集成异步线程调用

①：在自动审核的方法上加上@Async注解（标明要异步调用）

```java
@Override
@Async  //标明当前方法是一个异步方法
public void autoScanWmNews(Integer id) {
	//代码略
}
```

②：在文章发布成功后调用审核的方法

```java
@Autowired
private WmNewsAutoScanService wmNewsAutoScanService;

/**
 * 发布修改文章或保存为草稿
 * @param dto
 * @return
 */
@Override
public ResponseResult submitNews(WmNewsDto dto) {

    //代码略

    //审核文章
    wmNewsAutoScanService.autoScanWmNews(wmNews.getId());

    return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);

}
```

③：在自媒体引导类中使用@EnableAsync注解开启异步调用

```java
@SpringBootApplication
@EnableDiscoveryClient
@MapperScan("com.heima.wemedia.mapper")
@EnableFeignClients(basePackages = "com.heima.apis")
@EnableAsync  //开启异步调用
public class WemediaApplication {

    public static void main(String[] args) {
        SpringApplication.run(WemediaApplication.class,args);
    }

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}
```

### 新需求-自管理敏感词

#### 需求分析

文章审核功能已经交付了，文章也能正常发布审核。突然，产品经理过来说要开会。

会议的内容核心有以下内容：

- 文章审核不能过滤一些敏感词：

  私人侦探、针孔摄象、信用卡提现、广告代理、代开发票、刻章办、出售答案、小额贷款…

需要完成的功能：

需要自己维护一套敏感词，在文章审核的时候，需要验证文章是否包含这些敏感词

#### 敏感词-过滤

技术选型

| **方案**               | **说明**                     |
| ---------------------- | ---------------------------- |
| 数据库模糊查询         | 效率太低                     |
| String.indexOf("")查找 | 数据库量大的话也是比较慢     |
| 全文检索               | 分词再匹配                   |
| DFA算法                | 确定有穷自动机(一种数据结构) |

#### DFA实现原理

DFA全称为：Deterministic Finite Automaton,即确定有穷自动机。

存储：一次性的把所有的敏感词存储到了多个map中，就是下图表示这种结构

敏感词：冰毒、大麻、大坏蛋

![image-20210524160517744](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20210524160517744.png)

检索的过程

![image-20210524160549596](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20210524160549596.png)



#### 自管理敏感词集成到文章审核中

①：创建敏感词表，导入资料中wm_sensitive到leadnews_wemedia库中

![image-20210524160611338](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20210524160611338.png)

```java
package com.heima.model.wemedia.pojos;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 敏感词信息表
 * </p>
 *
 * @author itheima
 */
@Data
@TableName("wm_sensitive")
public class WmSensitive implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 敏感词
     */
    @TableField("sensitives")
    private String sensitives;

    /**
     * 创建时间
     */
    @TableField("created_time")
    private Date createdTime;

}
```

②：拷贝对应的wm_sensitive的mapper到项目中

```java
package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.wemedia.pojos.WmSensitive;
import org.apache.ibatis.annotations.Mapper;


@Mapper
public interface WmSensitiveMapper extends BaseMapper<WmSensitive> {
}
```

③：在文章审核的代码中添加自管理敏感词审核

第一：在WmNewsAutoScanServiceImpl中的autoScanWmNews方法上添加如下代码

```java
//从内容中提取纯文本内容和图片
//.....省略

//自管理的敏感词过滤
boolean isSensitive = handleSensitiveScan((String) textAndImages.get("content"), wmNews);
if(!isSensitive) return;

//2.审核文本内容  阿里云接口
//.....省略
```

新增自管理敏感词审核代码

```java
@Autowired
private WmSensitiveMapper wmSensitiveMapper;

/**
     * 自管理的敏感词审核
     * @param content
     * @param wmNews
     * @return
     */
private boolean handleSensitiveScan(String content, WmNews wmNews) {

    boolean flag = true;

    //获取所有的敏感词
    List<WmSensitive> wmSensitives = wmSensitiveMapper.selectList(Wrappers.<WmSensitive>lambdaQuery().select(WmSensitive::getSensitives));
    List<String> sensitiveList = wmSensitives.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());

    //初始化敏感词库
    SensitiveWordUtil.initMap(sensitiveList);

    //查看文章中是否包含敏感词
    Map<String, Integer> map = SensitiveWordUtil.matchWords(content);
    if(map.size() >0){
        updateWmNews(wmNews,(short) 2,"当前文章中存在违规内容"+map);
        flag = false;
    }

    return flag;
}
```

### 新需求-图片识别文字审核敏感词

#### 需求分析

产品经理召集开会，文章审核功能已经交付了，文章也能正常发布审核。对于上次提出的自管理敏感词也很满意，这次会议核心的内容如下：

- 文章中包含的图片要识别文字，过滤掉图片文字的敏感词

![image-20210524161243572](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20210524161243572.png)



#### 图片文字识别

什么是OCR?

OCR （Optical Character Recognition，光学字符识别）是指电子设备（例如扫描仪或数码相机）检查纸上打印的字符，通过检测暗、亮的模式确定其形状，然后用字符识别方法将形状翻译成计算机文字的过程

| **方案**      | **说明**                                            |
| ------------- | --------------------------------------------------- |
| 百度OCR       | 收费                                                |
| Tesseract-OCR | Google维护的开源OCR引擎，支持Java，Python等语言调用 |
| Tess4J        | 封装了Tesseract-OCR  ，支持Java调用                 |

#### Tess4j案例

①：创建项目导入tess4j对应的依赖

```xml
<dependency>
    <groupId>net.sourceforge.tess4j</groupId>
    <artifactId>tess4j</artifactId>
    <version>4.1.1</version>
</dependency>
```

②：导入中文字体库， 把资料中的tessdata文件夹拷贝到自己的工作空间下

![image-20210524161406081](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20210524161406081.png)



③：编写测试类进行测试

```java
package com.heima.tess4j;

import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

import java.io.File;

public class Application {

    public static void main(String[] args) {
        try {
            //获取本地图片
            File file = new File("D:\\26.png");
            //创建Tesseract对象
            ITesseract tesseract = new Tesseract();
            //设置字体库路径
            tesseract.setDatapath("D:\\workspace\\tessdata");
            //中文识别
            tesseract.setLanguage("chi_sim");
            //执行ocr识别
            String result = tesseract.doOCR(file);
            //替换回车和tal键  使结果为一行
            result = result.replaceAll("\\r|\\n","-").replaceAll(" ","");
            System.out.println("识别的结果为："+result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
```

#### 管理敏感词和图片文字识别集成到文章审核

①：在heima-leadnews-common中创建工具类，简单封装一下tess4j

需要先导入pom

```xml
<dependency>
    <groupId>net.sourceforge.tess4j</groupId>
    <artifactId>tess4j</artifactId>
    <version>4.1.1</version>
</dependency>
```

工具类

```java
package com.heima.common.tess4j;

import lombok.Getter;
import lombok.Setter;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "tess4j")
public class Tess4jClient {

    private String dataPath;
    private String language;

    public String doOCR(BufferedImage image) throws TesseractException {
        //创建Tesseract对象
        ITesseract tesseract = new Tesseract();
        //设置字体库路径
        tesseract.setDatapath(dataPath);
        //中文识别
        tesseract.setLanguage(language);
        //执行ocr识别
        String result = tesseract.doOCR(image);
        //替换回车和tal键  使结果为一行
        result = result.replaceAll("\\r|\\n", "-").replaceAll(" ", "");
        return result;
    }

}
```

在spring.factories配置中添加该类,完整如下：

```java
org.springframework.boot.autoconfigure.EnableAutoConfiguration=\
  com.heima.common.exception.ExceptionCatch,\
  com.heima.common.swagger.SwaggerConfiguration,\
  com.heima.common.swagger.Swagger2Configuration,\
  com.heima.common.aliyun.GreenTextScan,\
  com.heima.common.aliyun.GreenImageScan,\
  com.heima.common.tess4j.Tess4jClient
```

②：在heima-leadnews-wemedia中的配置中添加两个属性

```yaml
tess4j:
  data-path: D:\workspace\tessdata
  language: chi_sim
```

③：在WmNewsAutoScanServiceImpl中的handleImageScan方法上添加如下代码

```java
try {
    for (String image : images) {
        byte[] bytes = fileStorageService.downLoadFile(image);

        //图片识别文字审核---begin-----

        //从byte[]转换为butteredImage
        ByteArrayInputStream in = new ByteArrayInputStream(bytes);
        BufferedImage imageFile = ImageIO.read(in);
        //识别图片的文字
        String result = tess4jClient.doOCR(imageFile);

        //审核是否包含自管理的敏感词
        boolean isSensitive = handleSensitiveScan(result, wmNews);
        if(!isSensitive){
            return isSensitive;
        }

        //图片识别文字审核---end-----


        imageList.add(bytes);

    } 
}catch (Exception e){
    e.printStackTrace();
}
```

### 文章详情-静态文件生成

#### 思路分析

文章端创建app相关文章时，生成文章详情静态页上传到MinIO中

![image-20210709110852966](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20210709110852966.png)

#### 实现步骤

1.新建ArticleFreemarkerService创建静态文件并上传到minIO中

```java
package com.heima.article.service;

import com.heima.model.article.pojos.ApArticle;

public interface ArticleFreemarkerService {

    /**
     * 生成静态文件上传到minIO中
     * @param apArticle
     * @param content
     */
    public void buildArticleToMinIO(ApArticle apArticle,String content);
}
```

实现

```java
package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.pojos.ApArticle;
import freemarker.template.Configuration;
import freemarker.template.Template;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@Transactional
public class ArticleFreemarkerServiceImpl implements ArticleFreemarkerService {

    @Autowired
    private ApArticleContentMapper apArticleContentMapper;

    @Autowired
    private Configuration configuration;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private ApArticleService apArticleService;

    /**
     * 生成静态文件上传到minIO中
     * @param apArticle
     * @param content
     */
    @Async
    @Override
    public void buildArticleToMinIO(ApArticle apArticle, String content) {
        //已知文章的id
        //4.1 获取文章内容
        if(StringUtils.isNotBlank(content)){
            //4.2 文章内容通过freemarker生成html文件
            Template template = null;
            StringWriter out = new StringWriter();
            try {
                template = configuration.getTemplate("article.ftl");
                //数据模型
                Map<String,Object> contentDataModel = new HashMap<>();
                contentDataModel.put("content", JSONArray.parseArray(content));
                //合成
                template.process(contentDataModel,out);
            } catch (Exception e) {
                e.printStackTrace();
            }

            //4.3 把html文件上传到minio中
            InputStream in = new ByteArrayInputStream(out.toString().getBytes());
            String path = fileStorageService.uploadHtmlFile("", apArticle.getId() + ".html", in);


            //4.4 修改ap_article表，保存static_url字段
            apArticleService.update(Wrappers.<ApArticle>lambdaUpdate().eq(ApArticle::getId,apArticle.getId())
                    .set(ApArticle::getStaticUrl,path));


        }
    }

}
```

2.在ApArticleService的saveArticle实现方法中添加调用生成文件的方法

```java
@Autowired  
private ArticleFreemarkerService articleFreemarkerService;

/**
     * 保存app端相关文章
     * @param dto
     * @return
     */
@Override
public ResponseResult saveArticle(ArticleDto dto) {

    //        try {
    //            Thread.sleep(3000);
    //        } catch (InterruptedException e) {
    //            e.printStackTrace();
    //        }
    //1.检查参数
    if(dto == null){
        return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
    }

    ApArticle apArticle = new ApArticle();
    BeanUtils.copyProperties(dto,apArticle);

    //2.判断是否存在id
    if(dto.getId() == null){
        //2.1 不存在id  保存  文章  文章配置  文章内容

        //保存文章
        save(apArticle);

        //保存配置
        ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
        apArticleConfigMapper.insert(apArticleConfig);

        //保存 文章内容
        ApArticleContent apArticleContent = new ApArticleContent();
        apArticleContent.setArticleId(apArticle.getId());
        apArticleContent.setContent(dto.getContent());
        apArticleContentMapper.insert(apArticleContent);

    }else {
        //2.2 存在id   修改  文章  文章内容

        //修改  文章
        updateById(apArticle);

        //修改文章内容
        ApArticleContent apArticleContent = apArticleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, dto.getId()));
        apArticleContent.setContent(dto.getContent());
        apArticleContentMapper.updateById(apArticleContent);
    }

    //异步调用 生成静态文件上传到minio中
    articleFreemarkerService.buildArticleToMinIO(apArticle,dto.getContent());


    //3.结果返回  文章的id
    return ResponseResult.okResult(apArticle.getId());
}
```

3.文章微服务开启异步调用

![image-20210709111445360](https://raw.githubusercontent.com/YuyanCai/imagebed/main/note/image-20210709111445360.png)































