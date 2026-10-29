package com.ruoyi.web.config;

import com.ruoyi.framework.web.service.TokenService;
import com.ruoyi.web.filter.TravelAdminAuthFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 管理端专属配置
 */
@Configuration
public class TravelAdminConfig {

    @Autowired
    private TokenService tokenService;

    @Bean
    public TravelAdminAuthFilter travelAdminAuthFilter() {
        return new TravelAdminAuthFilter(tokenService);
    }

    @Bean
    public FilterRegistrationBean<TravelAdminAuthFilter> travelAdminAuthFilterRegistration() {
        FilterRegistrationBean<TravelAdminAuthFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(travelAdminAuthFilter());
        registrationBean.addUrlPatterns("/travel/aa/*");
        registrationBean.setName("travelAdminAuthFilter");
        registrationBean.setOrder(10);
        System.out.println("[TravelAdminConfig] 注册过滤器，URL模式: /travel/aa/*");
        return registrationBean;
    }
}
