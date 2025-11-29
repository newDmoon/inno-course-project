package org.innowise.orderservice.config;


import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.innowise.orderservice.util.ApplicationConstant;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate template) {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getCredentials() instanceof String token) {
            template.header(ApplicationConstant.AUTHORIZATION_HEADER,
                    ApplicationConstant.BEARER_PREFIX + token);
        }
    }
}
