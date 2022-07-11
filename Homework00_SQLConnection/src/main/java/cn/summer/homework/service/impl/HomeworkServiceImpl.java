package cn.summer.homework.service.impl;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.QuestionResultDTO;
import cn.summer.homework.DTO.ResultQuestionDTO;
import cn.summer.homework.Entity.Course;
import cn.summer.homework.Entity.Question;
import cn.summer.homework.Entity.Result;
import cn.summer.homework.Entity.User;
import cn.summer.homework.PO.*;
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
import org.springframework.jdbc.SQLWarningException;
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

    @Override
    @Transactional(rollbackFor = SQLWarningException.class)
    public HomeworkOpBO deleteQuestion(Integer tid, Integer qid)
            throws SQLRWException {
        HomeworkOpBO homeworkOpBO = new HomeworkOpBO();
        int flag = 0;
        int[] delete = new int[8];

        try {
            if (!userService.isTeacher(tid)) {
                throw new Exception("用户不存在/用户权限不够");
            }
            Question question = questionDao.selectByID(qid);
            if (question == null) {
                throw new Exception("问题不存在");
            }
            if (teacherQuestionDao.accurateSelect(new TeacherQuestion(tid, qid)) <= 0) {
                throw new Exception("用户权限不够");
            }
            Map<String, Object> map = new HashMap<>();
            flag = 1;
            delete[0] = 0;
            delete[1] = 0;
            delete[2] = 0;
            questionResultDao.selectByQID(qid).forEach(e -> {
                Result result = resultDao.selectByID(e);
                map.put("Result".concat(result.getId().toString()),
                        getRQ_DTO(result));
                delete[0] += studentResultDao.deleteByRID(e);
                delete[1] += resultCourseDao.deleteByRID(e);
                delete[2] += resultDao.deleteByID(e);
            });
            delete[3] = questionResultDao.deleteByQID(qid);
            flag = 2;
            delete[4] = question_typeDao.deleteByQID(qid);
            flag = 3;
            delete[5] = questionCourseDao.deleteByQID(qid);
            flag = 4;
            delete[6] = teacherQuestionDao.deleteByQID(qid);
            flag = 5;
            map.put("Question".concat(qid.toString()), getQR_DTO(question));
            delete[7] = questionDao.deleteByID(qid);
            flag = 6;
            logger.info("QuestionID: {} 删除完成", qid);
            logger.info("Result 部分");
            logger.info("StudentResult 删除了 {} 条数据", delete[0]);
            logger.info("ResultCourse 删除了 {} 条数据", delete[1]);
            logger.info("Result 删除了 {} 条数据", delete[2]);
            logger.info("QuestionResult 删除了 {} 条数据", delete[3]);
            logger.info("Question 部分");
            logger.info("Question_Type 删除了 {} 条数据", delete[4]);
            logger.info("QuestionCourse 删除了 {} 条数据", delete[5]);
            logger.info("TeacherQuestion 删除了 {} 条数据", delete[6]);
            logger.info("Question 删除了 {} 条数据", delete[7]);
            setHomeworkOpBO_Q(homeworkOpBO, map);
        } catch (Exception ex) {
            logger.error("QuestionID: {} 删除异常", qid);
            switch (flag) {
                case 0 -> logger.error("查询异常: {}", ex.getMessage());
                case 1 -> logger.error("Result 删除异常: {}", ex.getMessage());
                case 2 -> logger.error("Question_Type 删除异常: {}", ex.getMessage());
                case 3 -> logger.error("QuestionCourse 删除异常: {}", ex.getMessage());
                case 4 -> logger.error("TeacherQuestion 删除异常: {}", ex.getMessage());
                case 5 -> logger.error("Question 删除异常: {}", ex.getMessage());
                default -> logger.error("未知异常: {}", ex.getMessage());
            }
            if (flag >= 1 && flag <= 5) {
                throw new SQLRWException("Question 删除异常");
            }
            setHomeworkOpBO_Q(homeworkOpBO,
                    flag == 6 ? "Question 删除完成, 但仍有异常" :
                            "Question 删除失败",
                    ex.getMessage());
        }
        return homeworkOpBO;
    }

    private void updateExLogQuestion(HomeworkOpBO homeworkOpBO, int flag, Exception ex)
            throws SQLRWException {
        switch (flag) {
            case 0 -> logger.error("查询异常: {}", ex.getMessage());
            case 1 -> logger.error("Question 更新异常: {}", ex.getMessage());
            case 2 -> logger.error("Question_Type 更新异常: {}", ex.getMessage());
            default -> logger.error("未知异常: {}", ex.getMessage());
        }
        if (flag == 1 || flag == 2) {
            throw new SQLRWException("Question/Question_Type 删除异常");
        }
        setHomeworkOpBO_Q(homeworkOpBO,
                flag == 3 ? "Question/Question_Type 删除完成, 但仍有异常" :
                        "Question/TQuestion_Type 删除失败",
                ex.getMessage());
    }

    @Override
    @Transactional(rollbackFor = SQLWarningException.class)
    public HomeworkOpBO updateQuestion(Integer tid, Question question)
            throws SQLRWException {
        HomeworkOpBO homeworkOpBO = new HomeworkOpBO();
        int flag = 0;
        Integer id = question.getId();
        try {
            if (!userService.isTeacher(tid)) {
                throw new Exception("用户不存在/用户权限不够");
            }
            Question srcQuestion = selectHKByQID(id).getQuestion();
            if (srcQuestion == null) {
                throw new Exception("问题不存在");
            }
            if (teacherQuestionDao.accurateSelect(new TeacherQuestion(tid, id)) <= 0) {
                throw new Exception("用户权限不够");
            }
            flag = 1;
            int update = questionDao.updateQuestion(question);
            flag = 3;
            logger.info("QuestionID : {} 更新完成", id);
            logger.info("Question 更新了 {} 条数据", update);
            setHomeworkOpBO_Q(homeworkOpBO, new HashMap<>(2, 1f) {{
                QuestionResultDTO qr_dto = getQR_DTO(srcQuestion);
                put("srcQuestion", qr_dto);
                qr_dto.setQuestion(question);
                put("updateQuestion", qr_dto);
            }});
        } catch (Exception ex) {
            logger.error("QuestionID: {} 更新异常", id);
            updateExLogQuestion(homeworkOpBO, flag, ex);
        }
        return homeworkOpBO;
    }

    @Override
    @Transactional(rollbackFor = SQLWarningException.class)
    public HomeworkOpBO updateQuestion(Integer tid, Integer qid, String type)
            throws SQLRWException {
        HomeworkOpBO homeworkOpBO = new HomeworkOpBO();
        int flag = 0;

        try {
            if (!userService.isTeacher(tid)) {
                throw new Exception("用户不存在/用户权限不够");
            }
            if (selectHKByQID(qid).getQuestion() == null) {
                throw new Exception("问题不存在");
            }
            if (teacherQuestionDao.accurateSelect(new TeacherQuestion(tid, qid)) <= 0) {
                throw new Exception("用户权限不够");
            }
            Integer qtid = questionTypeDao.selectByName(type);
            if (qtid <= 0) {
                throw new Exception("问题类型不存在");
            }
            QuestionResultDTO src = getQR_DTO(qid);
            flag = 2;
            QuestionType questionType = new QuestionType(qid, qtid);
            int update;
            if (question_typeDao.accurateSelect(questionType) > 0) {
                update = question_typeDao.accurateDelete(questionType);
            } else {
                update = question_typeDao.createTypeOfQuestion(questionType);
            }
            flag = 3;
            logger.info("QuestionID: {} 更新完成", qid);
            logger.info("Question_Type 更新了 {} 条数据", update);
            setHomeworkOpBO_Q(homeworkOpBO, new HashMap<>(2, 1f) {{
                put("srcQuestion", src);
                put("updateQuestion", getQR_DTO(src.getQuestion()));
            }});
        } catch (Exception ex) {
            logger.error("QuestionID: {} 更新 问题类型 异常", qid);
            updateExLogQuestion(homeworkOpBO, flag, ex);
        }
        return homeworkOpBO;
    }

    @Override
    @Transactional(rollbackFor = SQLWarningException.class)
    public HomeworkOpBO updateQuestion(Integer tid, Question question, String type)
            throws SQLRWException {
        HomeworkOpBO homeworkOpBO = new HomeworkOpBO();
        int flag = 0;
        int[] update = new int[2];
        Integer qid = question.getId();

        try {
            if (!userService.isTeacher(tid)) {
                throw new Exception("用户不存在/用户权限不够");
            }
            Integer qtid = questionTypeDao.selectByName(type);
            if (qtid <= 0) {
                throw new Exception("问题类型不存在");
            }
            Question srcQuestion = selectHKByQID(qid).getQuestion();
            if (srcQuestion == null) {
                throw new Exception("问题不存在");
            }
            if (teacherQuestionDao.accurateSelect(new TeacherQuestion(tid, qid)) <= 0) {
                throw new Exception("用户权限不够");
            }
            QuestionResultDTO qr_dto = getQR_DTO(srcQuestion);
            flag = 1;
            update[0] = questionDao.updateQuestion(question);
            flag = 2;
            QuestionType questionType = new QuestionType(qid, qtid);
            if (question_typeDao.accurateSelect(questionType) > 0) {
                update[1] = question_typeDao.accurateDelete(questionType);
            } else {
                update[1] = question_typeDao.createTypeOfQuestion(questionType);
            }
            flag = 3;
            logger.info("QuestionID: {} 更新完成", qid);
            logger.info("Question 更新了 {} 条数据", update);
            logger.info("Question_Type 更新了 {} 条数据", update);
            setHomeworkOpBO_Q(homeworkOpBO, new HashMap<>(2, 1f) {{
                put("srcQuestion", qr_dto);
                put("updateQuestion", getQR_DTO(question));
            }});
        } catch (Exception ex) {
            logger.error("QuestionID: {} 更新异常", qid);
            updateExLogQuestion(homeworkOpBO, flag, ex);
        }
        return homeworkOpBO;
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public HomeworkOpBO correctResult(Integer tid, Integer qid, Result result)
            throws SQLRWException {
        HomeworkOpBO homeworkOpBO = new HomeworkOpBO();
        int flag = 0;
        int rid = result.getId();

        try {
            Result srcResult = resultDao.selectByID(rid);
            if (srcResult == null) {
                throw new Exception("回答不存在");
            }
            if (!result.getIsCheck()) {
                throw new Exception("未批改");
            }
            if (!userService.isTeacher(tid)) {
                throw new Exception("用户不存在/用户权限不够");
            }
            if (teacherQuestionDao.accurateSelect(new TeacherQuestion(tid, qid)) <= 0) {
                throw new Exception("用户权限不够");
            }
            if (questionResultDao.accurateSelect(new QuestionResult(qid, rid)) <= 0) {
                throw new Exception("答案与题不匹配");
            }
            flag = 1;
            int update = resultDao.updateResult(result);
            flag = 2;
            logger.info("TeacherID: {} 批改 QuestionID: {} ResultID: {} 完成", tid, qid, rid);
            logger.info("Result 更新 {} 条数据", update);
            setHomeworkOpBO_R(homeworkOpBO, new HashMap<>(2, 1f) {{
                put("srcResult", getRQ_DTO(srcResult));
                put("updateResult", getRQ_DTO(result));
            }});
        } catch (Exception ex) {
            logger.error("TeacherID: {} 批改 QuestionID: {} ResultID{} 异常", tid, qid, rid);
            switch (flag) {
                case 0 -> logger.error("查询异常: {}", ex.getMessage());
                case 1 -> logger.error("Result 更新异常: {}", ex.getMessage());
                default -> logger.error("未知异常: {}", ex.getMessage());
            }
            if (flag == 1) {
                throw new SQLRWException("Teacher 批改 Result 更新异常");
            }
            setHomeworkOpBO_R(homeworkOpBO,
                    flag == 2 ? "Result 更新完成, 但仍有异常" : "Result 更新失败",
                    ex.getMessage());
        }
        return homeworkOpBO;
    }

    @Override
    public List<String> getAllType() {
        return questionTypeDao.selectAll();
    }

    @Override
    @Transactional(rollbackFor = SQLWarningException.class)
    public boolean createType(Integer uid, String typeName) {
        if (userService.isAdmin(uid) || userService.isTeacher(uid)) {
            return questionTypeDao.addQuestionType(typeName) > 0;
        } else {
            logger.error("UserID: {} 不是管理员/教师", uid);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = SQLWarningException.class)
    public boolean deleteType(Integer uid, String typeName) {
        if (userService.isAdmin(uid) || userService.isTeacher(uid)) {
            return questionTypeDao.deleteQuestionType(typeName) > 0;
        } else {
            logger.error("UserID: {} 不是管理员/教师", uid);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = SQLWarningException.class)
    public boolean deleteType(Integer uid, Integer id) {
        if (userService.isAdmin(uid) || userService.isTeacher(uid)) {
            return questionTypeDao.deleteByID(id) > 0;
        } else {
            logger.error("UserID: {} 不是管理员/教师", uid);
            return false;
        }
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
    @Transactional(rollbackFor = SQLWarningException.class)
    public HomeworkOpBO answerQuestion(Integer sid, Integer cid, Integer qid, Result result)
            throws SQLRWException {
        HomeworkOpBO homeworkOpBO = new HomeworkOpBO();
        int flag = 0;
        int[] insert = new int[4];

        try {
            if (!userService.isStudent(sid)) {
                throw new Exception("用户不存在/用户权限不够");
            }
            if (!courseService.isLearningByStudent(cid, sid)) {
                throw new Exception("用户并未选修该课");
            }
            if (questionCourseDao.accurateDelete(new QuestionCourse(qid, cid)) <= 0) {
                throw new Exception("课程下没有指定问题");
            }
            flag = 1;
            insert[0] = resultDao.createNewResult(result);
            Integer rid = resultDao.getLast();
            Result finalResult = resultDao.selectByID(rid);
            flag = 2;
            insert[1] = questionResultDao.createResultOfQuestion(new QuestionResult(qid, rid));
            flag = 3;
            insert[2] = resultCourseDao.addResultOfCourse(new ResultCourse(rid, cid));
            flag = 4;
            insert[3] = studentResultDao.addNewResult(new StudentResult(sid, rid));
            flag = 5;
            logger.info("StudentID: {} 回答问题完成", sid);
            logger.info("Result 插入 {} 条数据", insert[0]);
            logger.info("QuestionResult 插入 {} 条数据", insert[1]);
            logger.info("ResultCourse 插入 {} 条数据", insert[2]);
            logger.info("StudentResult 插入 {} 条数据", insert[3]);
            setHomeworkOpBO_R(homeworkOpBO, new HashMap<>(1, 1f) {{
                put("新建Result", getRQ_DTO(finalResult));
            }});
        } catch (Exception ex) {
            logger.error("UserID: {} 新建回答异常", sid);
            switch (flag) {
                case 0 -> logger.error("查询异常: {}", ex.getMessage());
                case 1 -> logger.error("Result 插入异常: {}", ex.getMessage());
                case 2 -> logger.error("QuestionResult 插入异常: {}", ex.getMessage());
                case 3 -> logger.error("ResultCourse 插入异常: {}", ex.getMessage());
                case 4 -> logger.error("StudentResult 插入异常: {}", ex.getMessage());
                default -> logger.error("未知异常: {}", ex.getMessage());
            }
            if (flag >= 1 && flag <= 4) {
                throw new SQLRWException("新建 Result 异常");
            }
            setHomeworkOpBO_R(homeworkOpBO,
                    flag == 5 ? "新建 Result 完成, 但仍有异常" : "新建 Result 失败",
                    ex.getMessage());
        }
        return homeworkOpBO;
    }

    @Override
    @Transactional(rollbackFor = SQLWarningException.class)
    public HomeworkOpBO deleteResult(Integer sid, Integer rid)
            throws SQLRWException {
        HomeworkOpBO homeworkOpBO = new HomeworkOpBO();
        int flag = 0;
        int[] delete = new int[4];

        try {
            if (!userService.isStudent(sid)) {
                throw new Exception("用户不存在/用户权限不够");
            }
            Result srcResult = resultDao.selectByID(rid);
            if (srcResult == null) {
                throw new Exception("回答不存在");
            }
            if (studentResultDao.accurateSelect(new StudentResult(sid, rid)) <= 0) {
                throw new Exception("用户权限不够");
            }
            flag = 1;
            delete[3] = studentResultDao.accurateDelete(new StudentResult(sid, rid));
            flag = 2;
            delete[2] = resultCourseDao.deleteByRID(rid);
            flag = 3;
            delete[1] = questionResultDao.deleteByRID(rid);
            flag = 4;
            delete[0] = resultDao.deleteByID(rid);
            flag = 5;
            logger.info("StudentID: {} 回答问题完成", sid);
            logger.info("Result 删除 {} 条数据", delete[0]);
            logger.info("QuestionResult 删除 {} 条数据", delete[1]);
            logger.info("ResultCourse 删除 {} 条数据", delete[2]);
            logger.info("StudentResult 删除 {} 条数据", delete[3]);
            setHomeworkOpBO_R(homeworkOpBO, new HashMap<>(1, 1f) {{
                put("srcResult", getRQ_DTO(srcResult));
            }});
        } catch (Exception ex) {
            logger.error("UserID: {} 新建回答异常", sid);
            switch (flag) {
                case 0 -> logger.error("查询异常: {}", ex.getMessage());
                case 4 -> logger.error("Result 删除异常: {}", ex.getMessage());
                case 3 -> logger.error("QuestionResult 删除异常: {}", ex.getMessage());
                case 2 -> logger.error("ResultCourse 删除异常: {}", ex.getMessage());
                case 1 -> logger.error("StudentResult 删除异常: {}", ex.getMessage());
                default -> logger.error("未知异常: {}", ex.getMessage());
            }
            if (flag >= 1 && flag <= 4) {
                throw new SQLRWException("删除 Result 异常");
            }
            setHomeworkOpBO_R(homeworkOpBO,
                    flag == 5 ? "删除 Result 完成, 但仍有异常" : "删除 Result 失败",
                    ex.getMessage());
        }
        return homeworkOpBO;
    }

    @Override
    @Transactional(rollbackFor = SQLWarningException.class)
    public HomeworkOpBO updateResult(Integer sid, Result result)
            throws SQLRWException {
        HomeworkOpBO homeworkOpBO = new HomeworkOpBO();
        int flag = 0;
        Integer rid = result.getId();

        try {
            if (!userService.isStudent(sid)) {
                throw new Exception("用户不存在/用户权限不够");
            }
            Result srcResult = resultDao.selectByID(rid);
            if (srcResult == null) {
                throw new Exception("回答不存在");
            }
            if (studentResultDao.accurateSelect(new StudentResult(sid, rid)) <= 0) {
                throw new Exception("用户权限不够");
            }
            ResultQuestionDTO rq_dto = getRQ_DTO(srcResult);
            flag = 1;
            int update = resultDao.updateResult(result);
            flag = 2;
            logger.info("StudentID: {} 修改问题完成", sid);
            logger.info("Result 更新 {} 条数据", update);
            setHomeworkOpBO_R(homeworkOpBO, new HashMap<>(2, 1f) {{
                put("srcResult", rq_dto);
                put("updateResult", getRQ_DTO(result));
            }});
        } catch (Exception ex) {
            logger.error("UserID: {} 修改回答异常", sid);
            switch (flag) {
                case 0 -> logger.error("查询异常: {}", ex.getMessage());
                case 1 -> logger.error("Result 更新异常: {}", ex.getMessage());
                default -> logger.error("未知异常: {}", ex.getMessage());
            }
            if (flag == 1) {
                throw new SQLRWException("更新 Result 异常");
            }
            setHomeworkOpBO_R(homeworkOpBO,
                    flag == 2 ? "更新 Result 完成, 但仍有异常" : "更新 Result 失败",
                    ex.getMessage());
        }
        return homeworkOpBO;
    }
}
