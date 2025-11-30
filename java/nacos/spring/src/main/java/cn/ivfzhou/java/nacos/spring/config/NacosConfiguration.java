package cn.ivfzhou.java.nacos.spring.config;

import com.alibaba.nacos.api.annotation.NacosProperties;
import com.alibaba.nacos.spring.context.annotation.config.EnableNacosConfig;
import com.alibaba.nacos.spring.context.annotation.config.NacosPropertySource;
import com.alibaba.nacos.spring.context.annotation.discovery.EnableNacosDiscovery;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableNacosConfig(globalProperties = @NacosProperties(
        serverAddr = "ivfzhoudockernacos0:8848,ivfzhoudockernacos1:8848,ivfzhoudockernacos2:8848",
        username = "nacos", password = "nacos"))
@EnableNacosDiscovery(globalProperties = @NacosProperties(
        serverAddr = "ivfzhoudockernacos0:8848,ivfzhoudockernacos1:8848,ivfzhoudockernacos2:8848",
        username = "nacos", password = "nacos"))
@NacosPropertySource(dataId = "cn.ivfzhou.java.nacos.spring.key", autoRefreshed = true, groupId = "DEFAULT_GROUP")
public class NacosConfiguration {}
