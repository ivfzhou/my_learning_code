package cn.ivfzhou.java.springcloud.gatewayrouter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Mono;

@SpringBootApplication
@EnableDiscoveryClient
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class);
    }

    @Bean(name = "remoteAddrKeyResolver")
    public KeyResolver getKeyResolver() {
        // return exchange -> Mono.just(exchange.getRequest().getRemoteAddress().getHostName()); // 根据 IP 限流。
        return exchange -> Mono.just(exchange.getRequest().getPath().value()); // 根据 URI 限流。
    }

}
