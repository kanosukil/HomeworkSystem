package cn.summer.homework.service.impl;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.QuestionResultDTO;
import cn.summer.homework.DTO.ResultQuestionDTO;
import cn.summer.homework.Entity.Course;
import cn.summer.homework.Entity.Question;
import cn.summer.homework.Entity.Result;
import cn.summer.homework.Entity.User;
import cn.summer.homework.PO.QuestionCourse;
import cn.summer.homework.PO.QuestionType;
import cn.summer.homework.PO.TeacherQuestion;
import cn.summer.homework.dao.QuestionDao;
import cn.summer.homework.dao.QuestionTypeDao;
import cn.summer.homework.dao.ResultDao;
import cn.summer.homework.dao.cascade.*;
import cn.summer.homework.exception.SQLRWException;
import cn.summer.homework.service.CourseService;
import cn.summer.homework.service.HomeworkService;
import cn.summer.homework.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author VHBin
 * @date 2022/7/4-15:08
 */

@Service
public class HomeworkServiceImpl implements HomeworkService {
    private static final Logger logger = LoggerFactory.getLogger(HomeworkServiceImpl.class);
    @Resource
    private QuestionDao questionDao;
    @Resource
    private ResultDao resultDao;
    @Resource
    private QuestionTypeDao questionTypeDao;
    @Resource
    private Question_TypeDao question_typeDao;
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

    private User getUser(Integer uid) {
        return userService.findUser(uid).getUser();
    }

    private Question getQuestion(Integer qid) {
        return questionDao.selectByID(qid);
    }

    private Result getResult(Integer rid) {
        return resultDao.selectByID(rid);
    }

    private List<Integer> getResultsByQID(Integer qid) {
        return questionResultDao.selectByQID(qid);
    }

    private Integer getQuestionByRID(Integer rid) {
        return questionResultDao.selectByRID(rid);
    }

    private QuestionResultDTO getQR_DTO(Integer qid)
            throws Exception {
        // QuestionResultDTO(Question, Type, Teacher, Map<Student, Result>)
        // Question
        Question question = getQuestion(qid);
        if (!Objects.equals(question.getId(), qid)) {
            throw new Exception("问题 Question 不存在");
        }
        return getQR_DTO(question);
    }

    private QuestionResultDTO getQR_DTO(Question question) {
        Integer qid = question.getId();
        // Type
        StringBuilder type = new StringBuilder();
        question_typeDao.selectByQID(qid).forEach(e ->
                type.append(questionTypeDao.selectByID(e)));
        // Map<Student, Result>
        Map<User, Result> results = new HashMap<>();
        getResultsByQID(qid).forEach(e ->
                results.put(
                        getUser(studentResultDao.selectByRID(e)),
                        getResult(e))
        );
        return new QuestionResultDTO(question,
                type.toString(),
                getUser(teacherQuestionDao.selectByQID(qid)),
                results);
    }


    private ResultQuestionDTO getRQ_DTO(Integer rid)
            throws Exception {
        // ResultQuestionDTO(Result, Student, Map<Teacher, Map<Type, Question>>)
        // result
        Result result = getResult(rid);
        if (!Objects.equals(result.getId(), rid)) {
            throw new Exception("答案 Result 不存在");
        }
        return getRQ_DTO(result);
    }

    private ResultQuestionDTO getRQ_DTO(Result result) {
        Integer rid = result.getId();
        // type
        StringBuilder type = new StringBuilder();
        Integer qid = getQuestionByRID(rid);
        question_typeDao.selectByQID(qid).forEach(e ->
                type.append(questionTypeDao.selectByID(e))
                        .append(' '));
        // Map<Teacher, Map<Type, Question>>
        Map<User, Map<String, Question>> question
                = new HashMap<>(1, 1f) {{
            put(getUser(teacherQuestionDao.selectByQID(qid)),
                    // Map<Type, Question>
                    new HashMap<>(1, 1f) {{
                        put(type.toString(), getQuestion(qid));
                    }});
        }};
        return new ResultQuestionDTO(
                result,
                getUser(studentResultDao.selectByRID(rid)),
                question);
    }

    private void setHomeworkOpBO_Q(HomeworkOpBO homeworkOpBO_Q, String key, String value) {
        homeworkOpBO_Q.setIsSuccess(false);
        homeworkOpBO_Q.setIsQuestion(true);
        homeworkOpBO_Q.setInfo(new HashMap<>(1, 1f) {{
            put(key, value);
        }});
    }

    private void setHomeworkOpBO_Q(HomeworkOpBO homeworkOpBO_Q, Map<String, Object> map) {
        homeworkOpBO_Q.setIsSuccess(true);
        homeworkOpBO_Q.setIsQuestion(true);
        homeworkOpBO_Q.setInfo(map);
    }

    private void setHomeworkOpBO_R(HomeworkOpBO homeworkOpBO_Q, String key, String value) {
        homeworkOpBO_Q.setIsSuccess(false);
        homeworkOpBO_Q.setIsQuestion(false);
        homeworkOpBO_Q.setInfo(new HashMap<>(1, 1f) {{
            put(key, value);
        }});
    }

    private void setHomeworkOpBO_R(HomeworkOpBO homeworkOpBO_Q, Map<String, Object> map) {
        homeworkOpBO_Q.setIsSuccess(true);
        homeworkOpBO_Q.setIsQuestion(false);
        homeworkOpBO_Q.setInfo(map);
    }

    /*
    老师
     */
    @Override
    public List<QuestionResultDTO> selectAllHK_T() {
        List<QuestionResultDTO> res = new ArrayList<>();
        questionDao.selectAll().forEach(e -> res.add(getQR_DTO(e)));
        return res;
    }

    @Override
    public QuestionResultDTO selectHKByQID(Integer qid)
            throws Exception {
        return getQR_DTO(qid);
    }

    @Override
    public List<QuestionResultDTO> selectHKByTID(Integer tid)
            throws Exception {
        List<QuestionResultDTO> res = new ArrayList<>();
        if (userService.isTeacher(tid)) {
            for (Integer qid : teacherQuestionDao.selectByTID(tid)) {
                res.add(getQR_DTO(qid));
            }
        } else {
            throw new Exception("用户不存在/用户权限不够");
        }
        return res;
    }

    @Override
    public List<QuestionResultDTO> selectHKByQuestionType(String type)
            throws Exception {
        List<QuestionResultDTO> res = new ArrayList<>();
        for (Integer qid : question_typeDao.selectByQTID(questionTypeDao.selectByName(type))) {
            res.add(getQR_DTO(qid));
        }
        return res;
    }

    @Override
    public List<QuestionResultDTO> selectHKByCID_T(Integer cid)
            throws Exception {
        List<QuestionResultDTO> res = new ArrayList<>();
        for (Integer qid : questionCourseDao.selectByCID(cid)) {
            res.add(getQR_DTO(qid));
        }
        return res;
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public HomeworkOpBO createQuestion(Integer tid, Integer cid, Question question, String type)
            throws SQLRWException {
        HomeworkOpBO homeworkOpBO = new HomeworkOpBO();
        int flag = 0;
        int[] insert = new int[4];

        try {
            if (!userService.isTeacher(tid)) {
                throw new Exception("用户不存在/用户权限不够");
            }
            Course course = courseService.getCourse(cid).getCourse();
            if (!Objects.equals(course.getId(), cid)) {
                throw new Exception("课程不存在");
            }
            Integer qtid = questionTypeDao.selectByName(type);
            if (qtid <= 0) {
                throw new Exception("问题类型不存在");
            }
            if (!courseService.isTeachingByTeacher(tid, cid)) {
                throw new Exception("该课程并不由该老师教导");
            }
            flag = 1;
            insert[0] = questionDao.createNewQuestion(question);
            Question finalQuestion = questionDao.selectByID(questionDao.getLast());
            Integer finalQid = finalQuestion.getId();
            flag = 2;
            insert[1] = teacherQuestionDao.addNewQuestion(new TeacherQuestion(tid, finalQid));
            flag = 3;
            insert[2] = questionCourseDao.createQuestionOfCourse(new QuestionCourse(finalQid, cid));
            flag = 4;
            insert[3] = question_typeDao.createTypeOfQuestion(new QuestionType(finalQid, qtid));
            flag = 5;
            logger.info("创建 QuestionID: {} 完成", finalQid);
            logger.info("Question 表插入了 {} 条数据", insert[0]);
            logger.info("TeacherQuestion 表插入了 {} 条数据", insert[1]);
            logger.info("QuestionCourse 表插入了 {} 条数据", insert[2]);
            logger.info("Question_Type 表插入了 {} 条数据", insert[3]);
            setHomeworkOpBO_Q(homeworkOpBO, new HashMap<>(1, 1f) {{
                put("新建问题", getQR_DTO(finalQuestion));
            }});
        } catch (Exception ex) {
            logger.error("TID:{}, CID:{} 创建问题异常", tid, cid);
            switch (flag) {
                case 0 -> logger.error("查询异常: {}", ex.getMessage());
                case 1 -> logger.error("Question 插入异常: {}", ex.getMessage());
                case 2 -> logger.error("TeacherQuestion 插入异常: {}", ex.getMessage());
                case 3 -> logger.error("QuestionCourse 插入异常: {}", ex.getMessage());
                case 4 -> logger.error("Question_Type 插入异常: {}", ex.getMessage());
                default -> logger.error("未知异常: {}", ex.getMessage());
            }
            if (flag >= 1 && flag <= 4) {
                throw new SQLRWException("Question/TeacherQuestion/QuestionCourse/Question_Type 插入异常");
            }
            setHomeworkOpBO_Q(homeworkOpBO,
                    flag == 5 ? "Question/TeacherQuestion/QuestionCourse/Question_Type 插入完成, 但仍有异常" :
                            "Question/TeacherQuestion/QuestionCourse/Question_Type 插入失败",
                    ex.getMessage());
        }

        return homeworkOpBO;
    }

    private void deleteExLogQuestion(HomeworkOpBO homeworkOpBO, int flag, Exception ex)
            throws SQLRWException {
        switch (flag) {
            case 0 -> logger.error("查询异常: {}", ex.getMessage());
            case 1 -> logger.error("Question 删除异常: {}", ex.getMessage());
            case 2 -> logger.error("TeacherQuestion 删除异常: {}", ex.getMessage());
            case 3 -> logger.error("QuestionCourse 删除异常: {}", ex.getMessage());
            case 4 -> logger.error("Question_Type 删除异常: {}", ex.getMessage());
            default -> logger.error("未知异常: {}", ex.getMessage());
        }
        if (flag >= 1 && flag <= 4) {
            throw new SQLRWException("Question/TeacherQuestion/QuestionCourse/Question_Type 删除异常");
        }
        setHomeworkOpBO_Q(homeworkOpBO,
                flag == 5 ? "Question/TeacherQuestion/QuestionCourse/Question_Type 删除完成, 但仍有异常" :
                        "Question/TeacherQuestion/QuestionCourse/Question_Type 删除失败",
                ex.getMessage());
    }

    @Override
    public HomeworkOpBO deleteQuestion(Integer qid)
            throws SQLRWException {
        HomeworkOpBO homeworkOpBO = new HomeworkOpBO();
        int flag = 0;
        int[] delete = new int[8];

        try {
            if (questionDao.selectByID(qid) == null) {
                throw new Exception("问题不存在");
            }
            flag = 1;
            delete[0] = 0;
            delete[1] = 0;
            questionResultDao.selectByQID(qid).forEach(e -> {
                delete[0] += studentResultDao.deleteByRID(e);
                delete[1] += resultDao.deleteByID(e);
            });
            delete[2] = questionResultDao.deleteByQID(qid);
            flag = 2;
            delete[3] = question_typeDao.deleteByQID(qid);
            flag = 3;
            delete[4] = questionCourseDao.deleteByQID(qid);
            flag = 4;
        } catch (Exception ex) {
            logger.error("QuestionID: {} 删除异常", qid);
            deleteExLogQuestion(homeworkOpBO, flag, ex);
        }

        return homeworkOpBO;
    }

    @Override
    public HomeworkOpBO updateQuestion(Question question) throws SQLRWException {
        return null;
    }

    @Override
    public HomeworkOpBO updateQuestion(Integer qid, String type) throws SQLRWException {
        return null;
    }

    @Override
    public HomeworkOpBO updateQuestion(Question question, String type) throws SQLRWException {
        return null;
    }

    @Override
    public List<String> getAllType() {
        return questionTypeDao.selectAll();
    }

    @Override
    public boolean createType(String typeName) {
        return questionTypeDao.addQuestionType(typeName) > 0;
    }

    @Override
    public boolean deleteType(String typeName) {
        return questionTypeDao.deleteQuestionType(typeName) > 0;
    }

    @Override
    public boolean deleteType(Integer id) {
        return questionTypeDao.deleteByID(id) > 0;
    }

    /*
    学生
     */
    @Override
    public List<ResultQuestionDTO> selectAllHK_S() {
        List<ResultQuestionDTO> res = new ArrayList<>();
        resultDao.selectAll().forEach(e -> res.add(getRQ_DTO(e)));
        return res;
    }

    @Override
    public ResultQuestionDTO selectHKByRID(Integer rid)
            throws Exception {
        return getRQ_DTO(rid);
    }

    @Override
    public List<ResultQuestionDTO> selectHKBySID(Integer sid)
            throws Exception {
        List<ResultQuestionDTO> res = new ArrayList<>();
        for (Integer rid : studentResultDao.selectBySID(sid)) {
            res.add(getRQ_DTO(rid));
        }
        return res;
    }

    @Override
    public List<ResultQuestionDTO> selectHKByCID_S(Integer cid)
            throws Exception {
        List<ResultQuestionDTO> res = new ArrayList<>();
        for (Integer rid : resultCourseDao.selectByCID(cid)) {
            res.add(getRQ_DTO(rid));
        }
        return res;
    }

    @Override
    public HomeworkOpBO answerQuestion(Integer sid, Integer cid, Integer qid, Result result)
            throws SQLRWException {
        return null;
    }

    @Override
    public HomeworkOpBO deleteResult(Integer rid)
            throws SQLRWException {
        return null;
    }

    @Override
    public HomeworkOpBO updateResult(Result result)
            throws SQLRWException {
        return null;
    }
}
