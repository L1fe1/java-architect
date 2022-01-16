package com.l1fe1.feignclientconsumer;

import feign.FeignException;
import feign.hystrix.FallbackFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class ConsumerApiFallbackFactory implements FallbackFactory<ConsumerApi> {
    private static final Logger logger = LoggerFactory.getLogger(ConsumerApiFallbackFactory.class);

    @Override
    public ConsumerApi create(Throwable cause) {
        return new ConsumerApi() {
            @Override
            public String hello() {
                return null;
            }

            @Override
            public String timeoutRetry() {
                return null;
            }

            @Override
            public Map<Integer, String> getForParam(Integer id) {
                return null;
            }

            @Override
            public Map<String, Object> getForMultiParams(Map<String, Object> map) {
                return null;
            }

            @Override
            public String fallback() {
                logger.error(cause.getMessage());
                // 针对不同的异常进行处理
                if(cause instanceof FeignException.InternalServerError) {
                    logger.error("InternalServerError：{}", cause.getLocalizedMessage());
                    return "远程服务异常";
                } else if(cause instanceof RuntimeException) {
                    return "运行时异常：" + cause;
                }else {
                    return "其他异常";
                }
            }

            @Override
            public String zuul() {
                return null;
            }
        };
    }
}
