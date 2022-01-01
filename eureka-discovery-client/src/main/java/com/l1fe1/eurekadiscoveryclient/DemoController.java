package com.l1fe1.eurekadiscoveryclient;

import com.alibaba.fastjson.JSON;
import com.netflix.appinfo.InstanceInfo;
import com.netflix.discovery.EurekaClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.cloud.client.loadbalancer.LoadBalancerClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@RestController
public class DemoController {

    private static final Logger logger = LoggerFactory.getLogger(DemoController.class);

    // spring 官方定义的服务注册与发现抽象接口
    @Autowired
    private DiscoveryClient discoveryClient;
    @Qualifier("eurekaClient")
    @Autowired
    private EurekaClient eurekaClient;
    // spring 官方定义的负载均衡抽象接口
    @Autowired
    private LoadBalancerClient loadBalancerClient;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private RestTemplate lbRestTemplate;

    @GetMapping("/serviceInfo")
    public String serviceInfo() {
        // 获取服务信息
        List<String> services = discoveryClient.getServices();
        logger.info(JSON.toJSONString(services));
        // 获取实例信息
        List<ServiceInstance> instances = discoveryClient.getInstances("eureka-discovery-client");
        logger.info(JSON.toJSONString(instances));
        return "";
    }

    @GetMapping("/eureka/serviceInfo")
    public String eurekaServiceInfo() {
        // 获取指定服务 id 的实例信息
        List<InstanceInfo> instanceInfoList = eurekaClient.getInstancesById("DESKTOP-9K5TTA0:eureka-client-provider:8081");
        if (instanceInfoList.size() > 0) {
            InstanceInfo instanceInfo = instanceInfoList.get(0);
            logger.info(JSON.toJSONString(instanceInfo));
        }
        // 通过服务名获取实例列表
        List<InstanceInfo> instances = eurekaClient.getInstancesByVipAddress("eureka-client-provider", false);
        if (instances.size() > 0) {
            InstanceInfo instanceInfo = instances.get(0);
            if (InstanceInfo.InstanceStatus.UP.equals(instanceInfo.getStatus())) {
                String url = "http://" + instanceInfo.getHostName() + ":" + instanceInfo.getPort() + "/hello";
                RestTemplate restTemplate = new RestTemplate();
                ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
                String body = responseEntity.getBody();
                logger.info(body);
            }
        }
        // 客户端负载均衡，过滤掉了 DOWN 的节点
        ServiceInstance serviceInstance = loadBalancerClient.choose("eureka-client-provider");
        String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/hello";
        RestTemplate restTemplate = new RestTemplate();
        String response = restTemplate.getForObject(url, String.class);
        logger.info(response);
        return "";
    }

    @GetMapping("/loadBalance")
    public String loadBalance() {
        ServiceInstance serviceInstance = loadBalancerClient.choose("eureka-client-provider");
        String url = "http://" + serviceInstance.getHost() + ":" + serviceInstance.getPort() + "/port";
        String response = restTemplate.getForObject(url, String.class);
        return response;
    }

    @GetMapping("/loadBalanced")
    public String loadBalanced() {
        String url = "http://eureka-client-provider/port";
        String response = lbRestTemplate.getForObject(url, String.class);
        return response;
    }
}
