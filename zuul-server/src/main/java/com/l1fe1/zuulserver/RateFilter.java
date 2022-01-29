package com.l1fe1.zuulserver;

import com.google.common.util.concurrent.RateLimiter;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 限流 filter
 */
@Component
public class RateFilter extends ZuulFilter {

    private static final Logger logger = LoggerFactory.getLogger(RateFilter.class);

    // 每秒一个令牌，实际通过压测情况设置
    private static final RateLimiter RATE_LIMITER = RateLimiter.create(1);

    @Override
    public String filterType() {
        // 设置过滤器类型为前置过滤器
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        /**
         * 限流处理最先执行，因此需要设置的尽可能小
         */
        return -10;
    }

    @Override
    public boolean shouldFilter() {
        // 针对所有请求进行限流
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        logger.info("限流处理");
        RequestContext requestContext = RequestContext.getCurrentContext();
        if (!RATE_LIMITER.tryAcquire()) {
            logger.info("未获取到令牌，限流！");
            requestContext.setSendZuulResponse(false);
            requestContext.setResponseStatusCode(HttpStatus.TOO_MANY_REQUESTS.value());
        }
        return null;
    }
}
