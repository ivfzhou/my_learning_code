package cn.ivfzhou.java.springcloud.feignclient.config;

import java.io.IOException;
import java.time.Duration;

import feign.Logger;
import feign.Request;
import feign.Response;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class FeignLogger extends Logger {

    private final org.slf4j.Logger log = LoggerFactory.getLogger(FeignLogger.class);

    @Override
    protected boolean shouldLogRequestHeader(String header) {
        return false;
    }

    @Override
    protected boolean shouldLogResponseHeader(String header) {
        return false;
    }

    @Override
    protected void logRequest(String configKey, Level logLevel, Request request) {
        log.info("请求体 {}", new String(request.body()));
    }

    @Override
    protected Response logAndRebufferResponse(String configKey, Level logLevel, Response response, long elapsedTime)
            throws IOException {
        var body = response.body().asInputStream().readAllBytes();
        log.info("请求结果 {}, 耗时 {}", new String(body), Duration.ofMillis(elapsedTime));
        return response.toBuilder().body(body).build();
    }

    @Override
    protected void log(String configKey, String format, Object... args) {
        if (log.isInfoEnabled()) log.info(format, args);
    }

}
