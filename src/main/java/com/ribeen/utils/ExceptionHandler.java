package com.ribeen.utils;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 异常拦截处理
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018/12/18 15:00
 */
@Slf4j
@Component
public class ExceptionHandler implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
                                         Exception ex) {
        try {
            response.setContentType("application/json;charset=utf-8");
            response.setCharacterEncoding("utf-8");
            Result result = Result.error(ex.getMessage());
            //记录异常日志
            log.error(ex.getMessage(), ex);
            response.getWriter().print(JSON.toJSONString(result));
        } catch (Exception e) {
            log.error("ExceptionHandler 异常处理失败", e);
            e.printStackTrace();
        }
        return new ModelAndView();
    }
}
