package cn.ivfzhou.java.springcloud.gatewayrouter.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

// 使用代码配置路由。
//@Configuration
public class Router {

    @Bean
    public RouteLocator getRoutDefinition(RouteLocatorBuilder builder) {
        return builder
                .routes()
                .route("feign", predicateSpec -> predicateSpec
                        .path("/feign/**")
                        .filters(gatewayFilterSpec -> gatewayFilterSpec.stripPrefix(1))
                        .uri("lb://server")
                )
                .build();

    }

}
