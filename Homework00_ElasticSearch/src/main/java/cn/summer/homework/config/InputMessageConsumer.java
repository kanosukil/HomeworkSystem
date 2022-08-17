//package cn.summer.homework.config;
//
//import cn.summer.homework.Util.RabbitMQUtil;
//import org.springframework.cloud.stream.annotation.Input;
//import org.springframework.messaging.SubscribableChannel;
//import org.springframework.stereotype.Component;
//
///**
// * @author VHBin
// * @date 2022/8/12-18:29
// */
//@Component
//public interface InputMessageConsumer {
//    @Input(RabbitMQUtil.SAVE_IN)
//    SubscribableChannel saveChannel();
//
//    @Input(RabbitMQUtil.DELETE_IN)
//    SubscribableChannel deleteChannel();
//
//    @Input(RabbitMQUtil.UPDATE_IN)
//    SubscribableChannel updateChannel();
//}
