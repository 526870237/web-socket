package com.ribeen;

import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.ribeen.utils.Constant;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.http.HttpMessageConverters;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

/**
 * 用于启动Spring_boot项目
 *
 * @author paulandcode paulandcode@gmail.com
 * @since 2018年9月7日 上午8:36:52
 */
@SpringBootApplication
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    /**
     * 配置FastJson
     *
     * @return org.springframework.boot.autoconfigure.http.HttpMessageConverters
     */
    @Bean
    public HttpMessageConverters fastJsonHttpMessageConverters() {
        // 需要定义一个converter转换消息的对象
        FastJsonHttpMessageConverter converter = new FastJsonHttpMessageConverter();

        // 添加FastJSON的配置信息
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // 日期格式化, 只能使ResponseBody返回的日期格式化, 不能使JSON.toJsonString()格式化
        fastJsonConfig.setDateFormat(Constant.DATE_FORMAT);
        // 字符类型字段如果为null, 输出为"", 而非null
        fastJsonConfig.setSerializerFeatures(SerializerFeature.WriteNullStringAsEmpty);
        converter.setFastJsonConfig(fastJsonConfig);

        // 处理中文乱码
        List<MediaType> fastMediaTypes = new ArrayList<>();
        fastMediaTypes.add(MediaType.APPLICATION_JSON_UTF8);
        converter.setSupportedMediaTypes(fastMediaTypes);
        return new HttpMessageConverters(converter);
    }
}