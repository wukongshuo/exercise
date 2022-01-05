package com.xxw.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@Configuration
public class SecurityConfigurer extends WebMvcConfigurationSupport {

    @Autowired
    private SecurityProperties prop;

    public void addInterceptors(InterceptorRegistry registry) {
        //addPathPatterns 用于添加拦截规则
        //excludePathPatterns 用于排除拦截
        InterceptorRegistration registration = registry.addInterceptor(new AccessInterceptor(prop.getSecret()));

        String[] pathPatterns = prop.getPathPatterns();
        if (pathPatterns != null && pathPatterns.length > 0) {
            for (String pathPattern : pathPatterns) {
                registration.addPathPatterns(pathPattern);
            }
        }

        String[] excludePathPatterns = prop.getExcludePathPatterns();
        if (excludePathPatterns != null && excludePathPatterns.length > 0) {
            for (String excludePathPattern : excludePathPatterns) {
                registration.excludePathPatterns(excludePathPattern);
            }
        }
    }


    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
//		//将templates目录下的CSS、JS文件映射为静态资源，防止Spring把这些资源识别成thymeleaf模版
//		registry.addResourceHandler("/templates/**.js").addResourceLocations("classpath:/templates/");
//		registry.addResourceHandler("/templates/**.css").addResourceLocations("classpath:/templates/");
//		//其他静态资源
//		registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
        //swagger增加url映射
        registry.addResourceHandler("swagger-ui.html")
                .addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}