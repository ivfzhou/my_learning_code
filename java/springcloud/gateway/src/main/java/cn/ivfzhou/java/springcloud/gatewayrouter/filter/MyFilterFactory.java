package cn.ivfzhou.java.springcloud.gatewayrouter.filter;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;

@Component
public class MyFilterFactory extends AbstractGatewayFilterFactory<Object> {

    private final MyFilter filter = new MyFilter();

    @Override
    public GatewayFilter apply(Object config) {
        System.out.println("MyFilterFactory：" + config);
        return filter;
    }

    @Override
    public String name() {
        return "myFilter";
    }

}
