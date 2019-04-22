package com.ribeen.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 可跳转到Html页面, 类似jsp可以传递数据, 但同时又有Html功能
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/13 9:39
 */
@Controller
@RequestMapping(value = "html")
public class HtmlController {
    @RequestMapping(value = "websocket")
    public String webSocket() {
        return "webSocket";
    }

    @RequestMapping(value = "connection")
    public String connection() {
        return "connection";
    }

    @RequestMapping(value = "example")
    public String example() {
        return "example";
    }
}
