package cn.ivfzhou.java.nacos.springcloud.controller;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/config")
@RefreshScope
public class ConfigController {

    @Value("${key.key1:nil}")
    private String key;

    @Autowired
    private ConfigService configService;

    @RequestMapping("/get")
    @ResponseBody
    public String get() {
        return key;
    }

    @RequestMapping(value = "/getByDataId/{dataId}")
    @ResponseBody
    public String get(@PathVariable("dataId") String dataId) throws NacosException {
        return configService.getConfig(dataId, "DEFAULT_GROUP", 5000);
    }
}
