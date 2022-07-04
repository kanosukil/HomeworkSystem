package cn.summer.homework.service.impl;

import cn.summer.homework.dao.QuestionDao;
import cn.summer.homework.dao.ResultDao;
import cn.summer.homework.service.HomeworkService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/7/4-15:08
 */

@Service
public class HomeworkServiceImpl implements HomeworkService {
    @Resource
    private QuestionDao questionDao;
    @Resource
    private ResultDao resultDao;
}
