package cn.summer.homework.config;

import cn.summer.homework.Interceptor.AuthenticationInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/8/5-17:57
 */

@Configuration
public class InterceptorConfig implements WebMvcConfigurer {
    @Resource
    private AuthenticationInterceptor interceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(interceptor)
                .addPathPatterns("/**");
    }

}
