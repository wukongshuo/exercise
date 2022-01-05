package com.xxw.security;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.Arrays;

@Slf4j
public class FeignClientAuthConfiguration {
	
	
	@Autowired
	private SecurityProperties prop;
	
	
	@Bean
    public RequestInterceptor requestTokenBearerInterceptor() {

        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
            	long begin = System.currentTimeMillis();
            	
            	String authToken = Identity.builder().userId("System").userRoles(Arrays.asList("System"))
            		.build().generateJwtToken(prop.getSecret()).getToken();
            		
            	requestTemplate.header("Authorization", authToken); 
            	
            	log.debug("微服务之间调用生成Token花费的时间 {} 毫秒", System.currentTimeMillis() - begin);
            }
        };
    }
}
