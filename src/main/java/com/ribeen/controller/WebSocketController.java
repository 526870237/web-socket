package com.ribeen.controller;

import com.ribeen.service.WebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * web-socket配置
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/13 11:37
 */
@RestController
@RequestMapping(value = "websocket")
public class WebSocketController extends BaseController{
    @Autowired
    public WebSocketController(WebSocketService webSocketService) {
        // 给基础服务赋值, 否则无法使用增删改查
        baseService = webSocketService;
    }
}
