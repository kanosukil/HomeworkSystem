//package cn.summer.homework.config;
//
//import cn.summer.homework.Util.RabbitMQUtil;
//import lombok.NonNull;
//import org.springframework.amqp.core.Binding;
//import org.springframework.amqp.core.BindingBuilder;
//import org.springframework.amqp.core.DirectExchange;
//import org.springframework.amqp.core.Queue;
//import org.springframework.amqp.rabbit.connection.ConnectionFactory;
//import org.springframework.amqp.rabbit.core.RabbitAdmin;
//import org.springframework.beans.BeansException;
//import org.springframework.beans.factory.annotation.Qualifier;
//import org.springframework.beans.factory.config.BeanPostProcessor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author VHBin
// * @date 2022/8/8-22:24
// */
//
//@Configuration
//public class RabbitMQDirectConfig implements BeanPostProcessor {
//    //    @Resource
//    private RabbitAdmin rabbitAdmin;
//
//    @Bean
//    public RabbitAdmin rabbitAdmin(ConnectionFactory connectionFactory) {
//        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
//        rabbitAdmin.setAutoStartup(true);
//        this.rabbitAdmin = rabbitAdmin;
//        return rabbitAdmin;
//    }
//
//    @Bean
//    @Qualifier("save-queue")
//    public Queue saveDirectQueue() {
//        return new Queue(RabbitMQUtil.SAVE_QUEUE,
//                true, false, false);
//    }
//
//    @Bean
//    @Qualifier("save-exchange")
//    public DirectExchange saveDirectExchange() {
//        return new DirectExchange(RabbitMQUtil.SAVE_EXCHANGE,
//                true, false);
//    }
//
//    @Bean
//    @Qualifier("save-binding")
//    public Binding saveBindDirect(@Qualifier("save-queue") Queue queue,
//                                  @Qualifier("save-exchange") DirectExchange exchange) {
//        return BindingBuilder
//                .bind(queue)
//                .to(exchange)
//                .with(RabbitMQUtil.SAVE_ROUTING);
//    }
//
//    @Bean
//    @Qualifier("delete-queue")
//    public Queue deleteDirectQueue() {
//        return new Queue(RabbitMQUtil.DELETE_QUEUE,
//                true, false, false);
//    }
//
//    @Bean
//    @Qualifier("delete-exchange")
//    public DirectExchange deleteDirectExchange() {
//        return new DirectExchange(RabbitMQUtil.DELETE_EXCHANGE,
//                true, false);
//    }
//
//    @Bean
//    @Qualifier("delete-binding")
//    public Binding deleteBindDirect(@Qualifier("delete-queue") Queue queue,
//                                    @Qualifier("delete-exchange") DirectExchange exchange) {
//        return BindingBuilder
//                .bind(queue)
//                .to(exchange)
//                .with(RabbitMQUtil.DELETE_ROUTING);
//    }
//
//    @Bean
//    @Qualifier("update-queue")
//    public Queue updateDirectQueue() {
//        return new Queue(RabbitMQUtil.UPDATE_QUEUE,
//                true, false, false);
//    }
//
//    @Bean
//    @Qualifier("update-exchange")
//    public DirectExchange updateDirectExchange() {
//        return new DirectExchange(RabbitMQUtil.UPDATE_EXCHANGE,
//                true, false);
//    }
//
//    @Bean
//    @Qualifier("update-binding")
//    public Binding updateBindDirect(@Qualifier("update-queue") Queue queue,
//                                    @Qualifier("update-exchange") DirectExchange exchange) {
//        return BindingBuilder
//                .bind(queue)
//                .to(exchange)
//                .with(RabbitMQUtil.UPDATE_ROUTING);
//    }
//
//    @Override
//    public Object postProcessAfterInitialization(@NonNull Object bean, @NonNull String beanName)
//            throws BeansException {
//        rabbitAdmin.declareExchange(saveDirectExchange());
//        rabbitAdmin.declareExchange(deleteDirectExchange());
//        rabbitAdmin.declareExchange(updateDirectExchange());
//
//        rabbitAdmin.declareQueue(saveDirectQueue());
//        rabbitAdmin.declareQueue(deleteDirectQueue());
//        rabbitAdmin.declareQueue(updateDirectQueue());
//        return null;
//    }
//}
