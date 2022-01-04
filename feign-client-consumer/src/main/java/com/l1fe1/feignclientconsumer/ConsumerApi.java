package com.l1fe1.feignclientconsumer;

import com.l1fe1.feignapiprovider.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "feign-client-provider", fallback = ConsumerApiFallback.class)
public interface ConsumerApi extends UserApi {
}
