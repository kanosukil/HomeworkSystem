package cn.summer.homework.config;

import cn.summer.homework.Util.RabbitMQUtil;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/8/8-22:24
 */

@Configuration
public class RabbitMQDirectConfig implements BeanPostProcessor {
    @Resource
    private RabbitAdmin admin;

    @Bean
    public Queue saveDirectQueue() {
        return new Queue(RabbitMQUtil.SAVE_QUEUE,
                true, false, false);
    }

    @Bean
    public DirectExchange saveDirectExchange() {
        return new DirectExchange(RabbitMQUtil.SAVE_EXCHANGE,
                true, false);
    }

    @Bean
    public Binding saveBindDirect() {
        return BindingBuilder
                .bind(saveDirectQueue())
                .to(saveDirectExchange())
                .with(RabbitMQUtil.SAVE_ROUTING);
    }

    @Bean
    public Queue deleteDirectQueue() {
        return new Queue(RabbitMQUtil.DELETE_QUEUE,
                true, false, false);
    }

    @Bean
    public DirectExchange deleteDirectExchange() {
        return new DirectExchange(RabbitMQUtil.DELETE_EXCHANGE,
                true, false);
    }

    @Bean
    public Binding deleteBindDirect() {
        return BindingBuilder
                .bind(deleteDirectQueue())
                .to(deleteDirectExchange())
                .with(RabbitMQUtil.DELETE_ROUTING);
    }

    @Bean
    public Queue updateDirectQueue() {
        return new Queue(RabbitMQUtil.UPDATE_QUEUE,
                true, false, false);
    }

    @Bean
    public DirectExchange updateDirectExchange() {
        return new DirectExchange(RabbitMQUtil.UPDATE_EXCHANGE,
                true, false);
    }

    @Bean
    public Binding updateBindDirect() {
        return BindingBuilder
                .bind(updateDirectQueue())
                .to(updateDirectExchange())
                .with(RabbitMQUtil.UPDATE_ROUTING);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName)
            throws BeansException {
        admin.declareExchange(saveDirectExchange());
        admin.declareExchange(deleteDirectExchange());
        admin.declareExchange(updateDirectExchange());

        admin.declareQueue(saveDirectQueue());
        admin.declareQueue(deleteDirectQueue());
        admin.declareQueue(updateDirectQueue());
        return null;
    }
}
