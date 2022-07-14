package cn.summer.homework.controller;

import cn.summer.homework.feignClient.UserSQLClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/7/14-23:53
 */

@RefreshScope
@RestController
@RequestMapping("user")
public class UserServiceController {
    @Resource
    private UserSQLClient sqlClient;


}
