package com.ribeen.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 * Spring中添加WebSocketConfig的Bean
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 11/22/2018 7:40 AM
 */
@Configuration
public class WebSocketConfig {
    @Bean
    public ServerEndpointExporter serverEndpointExporter() {
        return new ServerEndpointExporter();
    }
}
