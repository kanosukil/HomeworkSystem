package cn.summer.homework.controller;

import cn.summer.homework.feignClient.ESReadClient;
import cn.summer.homework.service.FindService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/8/7-16:52
 */

@RestController
@RequestMapping("search")
public class SearchController {
    @Resource
    private FindService find;
    @Resource
    private ESReadClient es;
}
