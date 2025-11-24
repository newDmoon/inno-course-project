package org.innowise.authservice.config;

import feign.RequestInterceptor;
import org.innowise.authservice.util.ApplicationConstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientInterceptor {
    @Value("${security.internal-token}")
    private String internalToken;

    @Bean
    public RequestInterceptor internalFeignInterceptor() {
        return template -> template.header(
                ApplicationConstant.INTERNAL_HEADER,
                internalToken
        );
    }
}