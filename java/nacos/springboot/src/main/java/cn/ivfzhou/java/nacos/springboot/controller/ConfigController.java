package cn.ivfzhou.java.nacos.springboot.controller;

import com.alibaba.nacos.api.annotation.NacosInjected;
import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.annotation.NacosValue;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/config")
public class ConfigController {

    @NacosInjected
    private ConfigService configService;

    @NacosValue(value = "${key.key1:nil}", autoRefreshed = true)
    private String key;

    @RequestMapping(value = "/get")
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
