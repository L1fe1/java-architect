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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.servlet.http.HttpServletResponse;
import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.Map;

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
    @Autowired
    private RestTemplate lbInterceptorRestTemplate;

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

    @GetMapping("/restTemplate/entity")
    public String getForEntity() {
        String url = "http://eureka-client-provider/entity";
        ResponseEntity<String> entity = lbRestTemplate.getForEntity(url, String.class);
        logger.info(entity.toString());
        return entity.getBody();
    }

    @GetMapping("/restTemplate/map")
    public Object getForMap() {
        String url = "http://eureka-client-provider/map";
        ResponseEntity<Map> entity = lbRestTemplate.getForEntity(url, Map.class);
        logger.info(entity.getBody().toString());
        return entity.getBody();
    }

    @GetMapping("/restTemplate/param")
    public Object getForParam() {
        // 使用占位符传参
        String url = "http://eureka-client-provider/param?id={1}&name={2}";
        ResponseEntity<Person> entity = lbRestTemplate.getForEntity(url, Person.class, 1, "xiaoming");
        logger.info(entity.getBody().toString());
        // 使用 map 传参
        String urlUseMap = "http://eureka-client-provider/param?id={id}&name={name}";
        ResponseEntity<Person> entityUseMap = lbRestTemplate.getForEntity(urlUseMap, Person.class,
                2, "xiaohong");
        logger.info(entityUseMap.getBody().toString());
        return entity.getBody();
    }

    @PostMapping("/restTemplate/param")
    public Object postForParam() {
        String url = "http://eureka-client-provider/param";
        Person person = new Person();
        person.setId(3);
        person.setName("xiaowang");
        ResponseEntity<Person> entity = lbRestTemplate.postForEntity(url, person, Person.class);
        logger.info(entity.getBody().toString());
        return entity.getBody();
    }

    @PostMapping("/restTemplate/location")
    public Object postForLocation(HttpServletResponse response) throws Exception {
        String url = "http://eureka-client-provider/location";
        Map<String, String> map = Collections.singletonMap("name", "java");
        URI location = lbRestTemplate.postForLocation(url, map, Person.class);
        logger.info(location.toURL().toString());
        response.sendRedirect(location.toURL().toString());
        return location.toURL().toString();
    }

    @GetMapping("/restTemplate/exchange")
    public Object exchange() {
        String url = "http://eureka-client-provider/exchange";
        Person person = new Person();
        person.setId(4);
        person.setName("xiaoli");
        HttpHeaders headers = new HttpHeaders(); headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Person> entity = new HttpEntity<>(person, headers);
        ResponseEntity<Person> responseEntity = lbRestTemplate.exchange(url, HttpMethod.POST, entity, Person.class);
        return responseEntity.getBody().toString();
    }

    @GetMapping("/restTemplate/interceptor")
    public Object interceptor() {
        String url = "http://eureka-client-provider/hello";
        ResponseEntity<String> entity = lbInterceptorRestTemplate.getForEntity(url, String.class);
        return entity.getBody();
    }
}
