//package cn.summer.homework.config;
//
//import feign.Logger;
//import feign.codec.Encoder;
//import feign.form.spring.SpringFormEncoder;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.context.annotation.Primary;
//import org.springframework.context.annotation.Scope;
//
///**
// * @author VHBin
// * @date 2022/8/16-13:43
// */
//@Configuration
//public class FeignMultipartSupportConfig {
//    @Bean
//    @Primary
//    @Scope("prototype")
//    public Encoder multipartFormEncoder() {
//        return new SpringFormEncoder();
//    }
//
//    @Bean
//    public Logger.Level multipartLoggerLevel() {
//        return Logger.Level.FULL;
//    }
//}
