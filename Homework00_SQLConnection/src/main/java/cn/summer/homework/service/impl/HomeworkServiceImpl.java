package cn.summer.homework.service.impl;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.QuestionResultDTO;
import cn.summer.homework.DTO.ResultQuestionDTO;
import cn.summer.homework.Entity.Question;
import cn.summer.homework.Entity.Result;
import cn.summer.homework.dao.QuestionDao;
import cn.summer.homework.dao.QuestionTypeDao;
import cn.summer.homework.dao.ResultDao;
import cn.summer.homework.dao.cascade.*;
import cn.summer.homework.service.CourseService;
import cn.summer.homework.service.HomeworkService;
import cn.summer.homework.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
    @Resource
    private QuestionTypeDao questionTypeDao;
    @Resource
    private cn.summer.homework.dao.cascade.QuestionTypeDao qtDao;
    @Resource
    private QuestionResultDao questionResultDao;
    @Resource
    private QuestionCourseDao questionCourseDao;
    @Resource
    private ResultCourseDao resultCourseDao;
    @Resource
    private StudentResultDao studentResultDao;
    @Resource
    private TeacherQuestionDao teacherQuestionDao;
    @Resource
    private UserService userService;
    @Resource
    private CourseService courseService;

    @Override
    public List<QuestionResultDTO> selectAllHK_T() {
        List<QuestionResultDTO> res = new ArrayList<>();
        questionDao.selectAll().forEach(e -> {
            List<String> type = new ArrayList<>();
            qtDao.selectByQID(e.getId()).forEach(f -> type.add(questionTypeDao.selectByID(f)));
            res.add(new QuestionResultDTO(
                    e,
                    type.toString(),
                    userService.findUser(teacherQuestionDao.selectByQID(e.getId()))
            ));
        });
        return null;
    }

    @Override
    public QuestionResultDTO selectHKByQID(Integer qid) {
        return null;
    }

    @Override
    public List<QuestionResultDTO> selectHKByTID(Integer tid) {
        return null;
    }

    @Override
    public List<QuestionResultDTO> selectHKByQuestionType(String type) {
        return null;
    }

    @Override
    public List<QuestionResultDTO> selectHKByCID_T(Integer cid) {
        return null;
    }

    @Override
    public ResultQuestionDTO selectHKByQR(Integer qid, Integer rid) {
        return null;
    }

    @Override
    public HomeworkOpBO createQuestion(Integer tid, Integer cid, Question question, String type) {
        return null;
    }

    @Override
    public HomeworkOpBO deleteQuestion(Integer qid) {
        return null;
    }

    @Override
    public HomeworkOpBO updateQuestion(Question question) {
        return null;
    }

    @Override
    public HomeworkOpBO updateQuestion(Integer qid, String type) {
        return null;
    }

    @Override
    public HomeworkOpBO updateQuestion(Question question, String type) {
        return null;
    }

    @Override
    public List<ResultQuestionDTO> selectAllHK_S() {
        return null;
    }

    @Override
    public ResultQuestionDTO selectHKByRID(Integer rid) {
        return null;
    }

    @Override
    public List<ResultQuestionDTO> selectHKBySID(Integer sid) {
        return null;
    }

    @Override
    public List<ResultQuestionDTO> selectHKByCID_S(Integer cid) {
        return null;
    }

    @Override
    public HomeworkOpBO answerQuestion(Integer sid, Integer cid, Integer qid, Result result) {
        return null;
    }

    @Override
    public HomeworkOpBO deleteResult(Integer rid) {
        return null;
    }

    @Override
    public HomeworkOpBO updateResult(Result result) {
        return null;
    }
}
