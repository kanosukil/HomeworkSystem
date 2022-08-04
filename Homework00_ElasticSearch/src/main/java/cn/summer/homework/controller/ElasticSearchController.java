package cn.summer.homework.controller;

import cn.summer.homework.service.ElasticSearchService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/7/29-15:52
 */

@RestController
@RequestMapping("elastic-search")
public class ElasticSearchController {
    @Resource
    private ElasticSearchService elasticSearchService;
    
}
