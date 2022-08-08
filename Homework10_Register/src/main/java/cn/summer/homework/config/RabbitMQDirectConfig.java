package cn.summer.homework.config;

import cn.summer.homework.Util.RabbitMQUtil;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author VHBin
 * @date 2022/8/8-22:24
 */

@Configuration
public class RabbitMQDirectConfig {
    @Bean
    public Queue esDirectQueue() {
        return new Queue(RabbitMQUtil.RabbitDirectQueue,
                true, false, false);
    }

    @Bean
    public DirectExchange esDirectExchange() {
        return new DirectExchange(RabbitMQUtil.RabbitDirectExchange,
                true, false);
    }

    @Bean
    public Binding esBindDirect() {
        return BindingBuilder
                .bind(esDirectQueue())
                .to(esDirectExchange())
                .with(RabbitMQUtil.RabbitDirectRouting);
    }

//    @Bean
//    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
//        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
//        rabbitAdmin.setAutoStartup(true);
//        return rabbitAdmin;
//    }
}
