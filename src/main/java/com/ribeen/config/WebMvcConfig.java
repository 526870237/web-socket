package com.ribeen.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 自定义默认页面路径, 如登录页(默认: login), 主页(默认: index)等
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/18 10:55
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        // 可以直接跳转到html页面, 也可以重定向, 这里进行重定向
        registry.addViewController("/").setViewName("forward:html/websocket");
    }
}
