package cn.summer.homework.Utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author VHBin
 * @date 2022/7/3-11:39
 */

@Component
public class ApplicationContextUtils implements ApplicationContextAware {
    private static ApplicationContext app;

    // 获取 String AOP 容器对象, 并从中获得指定的 Bean 对象
    public static Object getBean(String beanName) {
        return app.getBean(beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        app = applicationContext;
    }
}
