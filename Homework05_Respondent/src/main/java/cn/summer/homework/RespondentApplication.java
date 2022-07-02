package cn.summer.homework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author VHBin
 * @date 2022/7/2-17:04
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
public class RespondentApplication {
    public static void main(String[] args) {
        SpringApplication.run(RespondentApplication.class, args);
    }
}
