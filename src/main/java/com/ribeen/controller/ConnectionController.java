package com.ribeen.controller;

import com.ribeen.service.ConnectionService;
import com.ribeen.utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 数据库连接
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 11/22/2018 3:05 PM
 */
@RestController
@RequestMapping(value = "connection")
public class ConnectionController extends BaseController {
    private final ConnectionService connectionService;

    @Autowired
    public ConnectionController(ConnectionService connectionService) {
        // 给基础服务赋值, 否则无法使用增删改查
        baseService = connectionService;
        this.connectionService = connectionService;
    }

    /**
     * http://127.0.0.1:8080/connection/getTables?connectionId=2
     *
     * @param params connectionId
     * @return com.ribeen.utils.Result
     */
    @RequestMapping(value = "getTables")
    public Result getTables(@RequestParam Map<String, String> params) {
        return connectionService.getTables(params);
    }

    /**
     * 测试连接是否成功
     *
     * @param params 参数
     * @return com.ribeen.utils.Result
     */
    @RequestMapping(value = "test")
    public Result test(@RequestParam Map<String, String> params) {
        return connectionService.test(params);
    }
}
