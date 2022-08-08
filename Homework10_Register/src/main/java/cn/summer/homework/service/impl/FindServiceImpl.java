package cn.summer.homework.service.impl;

import cn.summer.homework.DTO.CourseSTDTO;
import cn.summer.homework.DTO.QuestionResultDTO;
import cn.summer.homework.DTO.ResultQuestionDTO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.feignClient.CourseFindClient;
import cn.summer.homework.feignClient.QuestionFindClient;
import cn.summer.homework.feignClient.ResultFindClient;
import cn.summer.homework.service.FindService;
import cn.summer.homework.service.UserSearchService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/8-18:32
 */

@Service
public class FindServiceImpl implements FindService {
    @Resource
    private UserSearchService user;
    @Resource
    private CourseFindClient course;
    @Resource
    private QuestionFindClient question;
    @Resource
    private ResultFindClient result;

    @Override
    public List<UserRoleDTO> users() {
        return user.getAll();
    }

    @Override
    public UserRoleDTO user(Integer uid) throws IOException {
        return user.get(uid);
    }

    @Override
    public List<CourseSTDTO> courses() {
        return course.getAll();
    }

    @Override
    public CourseSTDTO course(Integer cid) throws IOException {
        return course.get(cid);
    }

    @Override
    public List<CourseSTDTO> courseBName(String name) throws IOException {
        return course.getn(name);
    }

    @Override
    public List<CourseSTDTO> courseBTeacher(Integer tid) throws IOException {
        return course.gett(tid);
    }

    @Override
    public List<CourseSTDTO> courseBStudent(Integer sid) throws IOException {
        return course.gets(sid);
    }

    @Override
    public List<QuestionResultDTO> questions() {
        return question.getAll();
    }

    @Override
    public QuestionResultDTO question(Integer qid) throws IOException {
        return question.get(qid);
    }

    @Override
    public List<QuestionResultDTO> questionBTeacher(Integer tid) throws IOException {
        return question.getTeacherQuestion(tid);
    }

    @Override
    public List<QuestionResultDTO> questionBType(String type) throws IOException {
        return question.getTypeQuestion(type);
    }

    @Override
    public List<QuestionResultDTO> questionBCourse(Integer cid) throws IOException {
        return question.getCourseQuestion(cid);
    }

    @Override
    public List<String> type() {
        return question.getAType();
    }

    @Override
    public List<ResultQuestionDTO> results() {
        return result.getAll();
    }

    @Override
    public ResultQuestionDTO result(Integer rid) throws IOException {
        return result.get(rid);
    }

    @Override
    public List<ResultQuestionDTO> resultBCourse(Integer cid) throws IOException {
        return result.getCourseResult(cid);
    }

    @Override
    public List<ResultQuestionDTO> resultBStudent(Integer sid) throws IOException {
        return result.getStudentResult(sid);
    }

}
