package com.l1fe1.eurekadiscoveryclient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class LoginClientHttpRequestInterceptor implements ClientHttpRequestInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LoginClientHttpRequestInterceptor.class);

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        logger.info("拦截请求:{}", request.getURI());
        ClientHttpResponse response = execution.execute(request, body);
        byte[] bytes = new byte[response.getBody().available()];
        response.getBody().read(bytes);
        logger.info("拦截响应内容:{}", new String(bytes, StandardCharsets.UTF_8));
        return response;
    }
}
