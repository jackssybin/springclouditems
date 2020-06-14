## 1.什么是网关
API网关是一个系统的唯一入口。
是众多分布式服务唯一的一个出口。
它做到了物理隔离,内网服务只有通过网关才能暴露到外网被别人访问。
简而言之:网关就是你家的大门

## 2.提供了哪些功能
1. 身份认证(oauth2/jwt)
2. 权限安全(黑白名单/爬虫控制)
3. 流量控制(请求大小/速率)
4. 数据转换(公共请求request/response)
5. 监控/metrics
6. 跨域问题(前后端分离)
7. 灰度发布(金丝雀发布/一部分去老客户端/一部分去新客户端)

## 3.市面上有哪些比较好的开源网关
1. [Nginx](http://nginx.org/)
2. [OpenResty](http://openresty.org/cn/)
3. [kong](https://konghq.com/)
4. [Spring Cloud Zuul/Gateway](https://spring.io/blog/2019/06/18/getting-started-with-spring-cloud-gateway)
5. [Zuul2](https://github.com/Netflix/zuul)



## 4.如何做一款网关(Spring Cloud Gateway)
1.Zuul和Gateway比较

```
Zuul: 是[netflix](https://www.netflix.com/cn/)开源的一个项目，Spring只是将Zuul集成在了Spring Cloud 中。

Gateway: Spring Cloud Gateway 是 Spring Cloud 的一个全新项目，该项目是基于 Spring 5.0，Spring Boot 2.0 和 Project Reactor 等技术开发的网关，它旨在为微服务架构提供一种简单有效的统一的 API 路由管理方式。
Spring Cloud Gateway 作为 Spring Cloud 生态系统中的网关，目标是替代 Netflix Zuul，其不仅提供统一的路由方式，并且基于 Filter 链的方式提供了网关基本的功能，例如：安全，监控/指标，和限流

```
2. 性能比较

```
网上很多地方都说Zuul是阻塞的，Gateway是非阻塞的，这么说是不严谨的，准确的讲Zuul1.x是阻塞的，
而在2.x的版本中，Zuul也是基于Netty，也是非阻塞的，如果一定要说性能，其实这个真没多大差距
```
而官方出过一个测试项目，创建了一个benchmark的测试项目：[spring-cloud-gateway-bench](https://github.com/spencergibb/spring-cloud-gateway-bench)，其中对比了：
Proxy | Avg Latency | Avg Req/Sec/Thread
-- | -- | -- 
gateway | 6.61ms | 3.24k
linkered | 7.62ms | 2.82k
zuul | 12.56ms | 2.09k
none | 2.09ms | 11.77k
从结果可知，Spring Cloud Gateway的RPS是Zuul1.x的1.55倍。


3. Spring Cloud GateWay 的特点和流程
```
**哪些特点**
> 基于 Spring Framework 5，Project Reactor 和 Spring Boot 2.0
> 动态路由
> Predicates 和 Filters 作用于特定路由
> 集成 Hystrix 断路器
> 集成 Spring Cloud DiscoveryClient
> 易于编写的 Predicates 和 Filters
> 限流
> 路径重写
```
**流程图**
![image](https://springcloud-oss.oss-cn-shanghai.aliyuncs.com/chapter12/006tKfTcly1fr2q2m5jq7j30cb0gjmxm.jpg)

```
客户端向 Spring Cloud Gateway 发出请求。然后在 Gateway Handler Mapping 中找到与请求相匹配的路由，
将其发送到 Gateway Web Handler。Handler 再通过指定的过滤器链来将请求发送到我们实际的服务执行业务逻辑，然后返回。
过滤器之间用虚线分开是因为过滤器可能会在发送代理请求之前（“pre”）或之后（“post”）执行业务逻辑。
```

4. 第一个网关
pom.xml

```
<dependency>
            <groupId>org.springframework.cloud</groupId>
            <artifactId>spring-cloud-starter-gateway</artifactId>
</dependency>
```
Application.java

```
@SpringBootApplication
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }
    
    // 等同于在yml文件中的配置
    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes().route(r -> r.path("/test/aa")
                .uri("http://www.baidu.com"))
                .route(r -> r.path("/baidu")
                        .uri("http://www.baidu.com"))
                .route(r -> r.path("/testa/**")
                        .uri("lb://servicea"))
                .build();
    }
}
```


#启动参数 支持的路由方式

```
Loaded RoutePredicateFactory [After]
2020-06-12 11:39:21.811  INFO 3680 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Before]
2020-06-12 11:39:21.811  INFO 3680 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Between]
2020-06-12 11:39:21.811  INFO 3680 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Cookie]
2020-06-12 11:39:21.811  INFO 3680 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Header]
2020-06-12 11:39:21.812  INFO 3680 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Host]
2020-06-12 11:39:21.812  INFO 3680 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Method]
2020-06-12 11:39:21.812  INFO 3680 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Path]
2020-06-12 11:39:21.812  INFO 3680 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Query]
2020-06-12 11:39:21.812  INFO 3680 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [ReadBodyPredicateFactory]
2020-06-12 11:39:21.812  INFO 3680 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [RemoteAddr]
2020-06-12 11:39:21.812  INFO 3680 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [Weight]
2020-06-12 11:39:21.812  INFO 3680 --- [           main] o.s.c.g.r.RouteDefinitionRouteLocator    : Loaded RoutePredicateFactory [CloudFoundryRouteService]
```
![路由方式](https://springcloud-oss.oss-cn-shanghai.aliyuncs.com/chapter12/spring-cloud-gateway3.png)



# 开启端点检查

访问url:
http://localhost:9002/actuator/gateway/routes

pom.xml添加
```
 <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-actuator</artifactId>
        </dependency>
```
#yml 添加配置 端点打开

```
management:
  endpoints:
    web:
      exposure:
        include: "*"
```

# 参考


```

https://blog.csdn.net/squirrelanimal0922/article/details/90517946

https://spring.io/guides/gs/gateway/

https://github.com/forezp/SpringCloudLearning/tree/master/sc-f-gateway-cloud

https://github.com/forezp/SpringCloudLearning


https://zhuanlan.zhihu.com/p/92460075

https://www.cnblogs.com/babycomeon/p/11161073.html
```
