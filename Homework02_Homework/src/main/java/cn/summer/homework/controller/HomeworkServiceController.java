package cn.summer.homework.controller;

import cn.summer.homework.feignClient.QuestionSQLClient;
import cn.summer.homework.feignClient.ResultSQLClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/7/14-23:59
 */

@RefreshScope
@RestController
@RequestMapping("homework")
public class HomeworkServiceController {
    @Resource
    private QuestionSQLClient questionClient;
    @Resource
    private ResultSQLClient resultClient;
}
