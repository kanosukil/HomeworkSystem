//package cn.summer.homework.config;
//
//import cn.summer.homework.Util.RabbitMQUtil;
//import org.springframework.cloud.stream.annotation.Output;
//import org.springframework.messaging.MessageChannel;
//import org.springframework.stereotype.Component;
//
///**
// * @author VHBin
// * @date 2022/8/12-18:03
// */
//@Component
//public interface OutputChannelProcessor {
//    @Output(RabbitMQUtil.SAVE_OUT)
//    MessageChannel saveChannel();
//
//    @Output(RabbitMQUtil.DELETE_OUT)
//    MessageChannel deleteChannel();
//
//    @Output(RabbitMQUtil.UPDATE_OUT)
//    MessageChannel updateChannel();
//}
