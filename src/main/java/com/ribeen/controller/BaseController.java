package com.ribeen.controller;

import com.ribeen.service.BaseService;
import com.ribeen.utils.Result;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;

/**
 * 基础增删改查Controller
 * 方法上的@RequestMapping在多个Controller上重复, 所以Controller类上的@RequestMapping必须有且不能重复
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/13 9:39
 */
public abstract class BaseController {
    /**
     * 基础服务, 需要在Spring自动构造Controller实体的时候给基础服务赋值
     */
    BaseService baseService;

    /**
     * 增, http://127.0.0.1:8080/connection/insert?url=111&username=222&password=333
     *
     * @param params url, username, password
     * @return com.ribeen.utils.Result
     */
    @RequestMapping(value = "insert", method = RequestMethod.POST)
    public Result insert(@RequestParam Map<String, String> params) {
        return baseService.insert(params);
    }

    /**
     * 删, http://127.0.0.1:8080/connection/delete?id=1
     *
     * @param params id
     * @return com.ribeen.utils.Result
     */
    @RequestMapping(value = "delete")
    public Result delete(@RequestParam Map<String, String> params) {
        return baseService.delete(params);
    }

    /**
     * 改, http://127.0.0.1:8080/connection/update?id=46faf8ebaabf47baca6&url=111&username=222&password=333
     *
     * @param params id, url, username, password
     * @return com.ribeen.utils.Result
     */
    @RequestMapping(value = "update", method = RequestMethod.POST)
    public Result update(@RequestParam Map<String, String> params) {
        return baseService.update(params);
    }

    /**
     * 查单个, http://127.0.0.1:8080/connection/info?id=2
     *
     * @param params id
     * @return com.ribeen.utils.Result
     */
    @RequestMapping(value = "info")
    public Result info(@RequestParam Map<String, String> params) {
        return baseService.info(params);
    }

    /**
     * 查列表, http://127.0.0.1:8080/connection/list
     *
     * @param params 无
     * @return com.ribeen.utils.Result
     */
    @RequestMapping(value = "list")
    public Result list(@RequestParam Map<String, String> params) {
        return baseService.list(params);
    }
}
