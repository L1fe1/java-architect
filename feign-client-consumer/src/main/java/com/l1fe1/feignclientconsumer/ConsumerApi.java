package com.l1fe1.feignclientconsumer;

import com.l1fe1.feignapiprovider.UserApi;
import org.springframework.cloud.openfeign.FeignClient;

@FeignClient("feign-client-provider")
public interface ConsumerApi extends UserApi {
}
