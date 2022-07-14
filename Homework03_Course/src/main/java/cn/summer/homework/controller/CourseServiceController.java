package cn.summer.homework.controller;

import cn.summer.homework.feignClient.CourseSQLClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/7/15-0:01
 */

@RefreshScope
@RestController
@RequestMapping("course")
public class CourseServiceController {
    @Resource
    private CourseSQLClient sqlClient;
}
