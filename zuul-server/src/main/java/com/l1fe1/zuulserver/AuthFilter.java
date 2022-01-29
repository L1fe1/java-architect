package com.l1fe1.zuulserver;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;

/**
 * 鉴权 filter
 */
@Component
public class AuthFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthFilter.class);

    @Override
    public String filterType() {
        // 设置过滤器类型为前置过滤器
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        /**
         * 设置为 4 的原因是需要在路由转发（{@link FilterConstants.PRE_DECORATION_FILTER_ORDER}）之前执行
         */
        return 4;
    }

    @Override
    public boolean shouldFilter() {
        // 获取当前上下文
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String uri = request.getRequestURI();
        logger.info("请求 uri：{}", uri);
        String needFilteredUri = "/feign-client-consumer/token";
        if (needFilteredUri.equalsIgnoreCase(uri)) {
            return true;
        }
        return false;
    }

    @Override
    public Object run() throws ZuulException {
        logger.info("拦截请求");
        RequestContext requestContext = RequestContext.getCurrentContext();
        HttpServletRequest request = requestContext.getRequest();
        String token = request.getHeader("token");
        if ("token-123".equals(token)) {
            logger.info("鉴权通过");
        } else {
            logger.info("鉴权不通过");
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.UNAUTHORIZED.value());
        }
        return null;
    }
}
