package com.example.news.config;


import com.example.news.types.NewsMain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.format.Formatter;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.persistence.Version;

//이곳에서 CORS 관련 설정을 한다.
@Configuration
public class WebConfig implements WebMvcConfigurer {


//    IpBanService ipBanService;

    @Override
    public void addCorsMappings(CorsRegistry corsRegistry) {
        corsRegistry.addMapping("/**")
                //.allowedOrigins("*")
                .allowedOriginPatterns("*")
                .allowedMethods("POST", "GET", "PUT", "DELETE", "HEAD", "OPTIONS") // 클라이언트에서 요청하는 메소드 어디까지 허용할 것인가.
                .allowCredentials(true);
    }

    @Override
    public void addFormatters(FormatterRegistry registry) {
        WebMvcConfigurer.super.addFormatters(registry);
        registry.addConverter(new NewsMainConverter());
    }

//    @Override
//    public void addInterceptors(InterceptorRegistry registry) {
//        HttpInterceptor httpInterceptor = new HttpInterceptor(ipBanService);
//        registry.addInterceptor(httpInterceptor);
//    }

    public class NewsMainConverter implements Converter<String, NewsMain> {
        @Override
        public NewsMain convert(String source) {
            return NewsMain.valueOf(source);
        }
    }

}