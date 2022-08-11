package cn.summer.homework;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;

/**
 * @author VHBin
 * @date 2022/8/11-13:56
 */

@SpringBootApplication
@EnableFeignClients
@EnableDiscoveryClient
public class FileStoreApplication {
    public static void main(String[] args) {
        SpringApplication.run(FileStoreApplication.class, args);
    }
}
