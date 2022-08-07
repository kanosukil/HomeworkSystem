package cn.summer.homework.controller;

import cn.summer.homework.feignClient.*;
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
    private CourseFindClient course;
    @Resource
    private QuestionFindClient question;
    @Resource
    private ResultFindClient result;
    @Resource
    private UserClient user;
    @Resource
    private ESReadClient es;
}
