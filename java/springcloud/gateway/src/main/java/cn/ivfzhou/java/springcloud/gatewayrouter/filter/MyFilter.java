package cn.ivfzhou.java.springcloud.gatewayrouter.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class MyFilter implements GatewayFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println("MyFilter：" + exchange.getRequest().getURI().getPath());
        // 放行。
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 100; // 值越小优先级越高。
    }

}
