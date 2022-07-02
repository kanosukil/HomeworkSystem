package cn.summer.homework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author VHBin
 * @date 2022/7/2-17:30
 */
@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class CourseManagerApplication {
    public static void main(String[] args) {
        SpringApplication.run(CourseManagerApplication.class, args);
    }
}
