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
            throw new Exception("?????? Question ?????????");
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
            throw new Exception("?????? Result ?????????");
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
    ??????
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
            throw new Exception("???????????????/??????????????????");
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
                throw new Exception("???????????????/??????????????????");
            }
            Course course = courseService.getCourse(cid).getCourse();
            if (!Objects.equals(course.getId(), cid)) {
                throw new Exception("???????????????");
            }
            Integer qtid = questionTypeDao.selectByName(type);
            if (qtid <= 0) {
                throw new Exception("?????????????????????");
            }
            if (!courseService.isTeachingByTeacher(tid, cid)) {
                throw new Exception("?????????????????????????????????");
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
            logger.info("?????? QuestionID: {} ??????", finalQid);
            logger.info("Question ???????????? {} ?????????", insert[0]);
            logger.info("TeacherQuestion ???????????? {} ?????????", insert[1]);
            logger.info("QuestionCourse ???????????? {} ?????????", insert[2]);
            logger.info("Question_Type ???????????? {} ?????????", insert[3]);
            setHomeworkOpBO_Q(homeworkOpBO, new HashMap<>(1, 1f) {{
                put("????????????", getQR_DTO(finalQuestion));
            }});
        } catch (Exception ex) {
            logger.error("TID:{}, CID:{} ??????????????????", tid, cid);
            switch (flag) {
                case 0 -> logger.error("????????????: {}", ex.getMessage());
                case 1 -> logger.error("Question ????????????: {}", ex.getMessage());
                case 2 -> logger.error("TeacherQuestion ????????????: {}", ex.getMessage());
                case 3 -> logger.error("QuestionCourse ????????????: {}", ex.getMessage());
                case 4 -> logger.error("Question_Type ????????????: {}", ex.getMessage());
                default -> logger.error("????????????: {}", ex.getMessage());
            }
            if (flag >= 1 && flag <= 4) {
                throw new SQLRWException("Question/TeacherQuestion/QuestionCourse/Question_Type ????????????");
            }
            setHomeworkOpBO_Q(homeworkOpBO,
                    flag == 5 ? "Question/TeacherQuestion/QuestionCourse/Question_Type ????????????, ???????????????" :
                            "Question/TeacherQuestion/QuestionCourse/Question_Type ????????????",
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
                throw new Exception("???????????????/??????????????????");
            }
            Question question = questionDao.selectByID(qid);
            if (question == null) {
                throw new Exception("???????????????");
            }
            if (teacherQuestionDao.accurateSelect(new TeacherQuestion(tid, qid)) <= 0) {
                throw new Exception("??????????????????");
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
            logger.info("QuestionID: {} ????????????", qid);
            logger.info("Result ??????");
            logger.info("StudentResult ????????? {} ?????????", delete[0]);
            logger.info("ResultCourse ????????? {} ?????????", delete[1]);
            logger.info("Result ????????? {} ?????????", delete[2]);
            logger.info("QuestionResult ????????? {} ?????????", delete[3]);
            logger.info("Question ??????");
            logger.info("Question_Type ????????? {} ?????????", delete[4]);
            logger.info("QuestionCourse ????????? {} ?????????", delete[5]);
            logger.info("TeacherQuestion ????????? {} ?????????", delete[6]);
            logger.info("Question ????????? {} ?????????", delete[7]);
            setHomeworkOpBO_Q(homeworkOpBO, map);
        } catch (Exception ex) {
            logger.error("QuestionID: {} ????????????", qid);
            switch (flag) {
                case 0 -> logger.error("????????????: {}", ex.getMessage());
                case 1 -> logger.error("Result ????????????: {}", ex.getMessage());
                case 2 -> logger.error("Question_Type ????????????: {}", ex.getMessage());
                case 3 -> logger.error("QuestionCourse ????????????: {}", ex.getMessage());
                case 4 -> logger.error("TeacherQuestion ????????????: {}", ex.getMessage());
                case 5 -> logger.error("Question ????????????: {}", ex.getMessage());
                default -> logger.error("????????????: {}", ex.getMessage());
            }
            if (flag >= 1 && flag <= 5) {
                throw new SQLRWException("Question ????????????");
            }
            setHomeworkOpBO_Q(homeworkOpBO,
                    flag == 6 ? "Question ????????????, ???????????????" :
                            "Question ????????????",
                    ex.getMessage());
        }
        return homeworkOpBO;
    }

    private void updateExLogQuestion(HomeworkOpBO homeworkOpBO, int flag, Exception ex)
            throws SQLRWException {
        switch (flag) {
            case 0 -> logger.error("????????????: {}", ex.getMessage());
            case 1 -> logger.error("Question ????????????: {}", ex.getMessage());
            case 2 -> logger.error("Question_Type ????????????: {}", ex.getMessage());
            default -> logger.error("????????????: {}", ex.getMessage());
        }
        if (flag == 1 || flag == 2) {
            throw new SQLRWException("Question/Question_Type ????????????");
        }
        setHomeworkOpBO_Q(homeworkOpBO,
                flag == 3 ? "Question/Question_Type ????????????, ???????????????" :
                        "Question/TQuestion_Type ????????????",
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
                throw new Exception("???????????????/??????????????????");
            }
            Question srcQuestion = selectHKByQID(id).getQuestion();
            if (srcQuestion == null) {
                throw new Exception("???????????????");
            }
            if (teacherQuestionDao.accurateSelect(new TeacherQuestion(tid, id)) <= 0) {
                throw new Exception("??????????????????");
            }
            flag = 1;
            int update = questionDao.updateQuestion(question);
            flag = 3;
            logger.info("QuestionID : {} ????????????", id);
            logger.info("Question ????????? {} ?????????", update);
            setHomeworkOpBO_Q(homeworkOpBO, new HashMap<>(2, 1f) {{
                QuestionResultDTO qr_dto = getQR_DTO(srcQuestion);
                put("srcQuestion", qr_dto);
                qr_dto.setQuestion(question);
                put("updateQuestion", qr_dto);
            }});
        } catch (Exception ex) {
            logger.error("QuestionID: {} ????????????", id);
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
                throw new Exception("???????????????/??????????????????");
            }
            if (selectHKByQID(qid).getQuestion() == null) {
                throw new Exception("???????????????");
            }
            if (teacherQuestionDao.accurateSelect(new TeacherQuestion(tid, qid)) <= 0) {
                throw new Exception("??????????????????");
            }
            Integer qtid = questionTypeDao.selectByName(type);
            if (qtid <= 0) {
                throw new Exception("?????????????????????");
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
            logger.info("QuestionID: {} ????????????", qid);
            logger.info("Question_Type ????????? {} ?????????", update);
            setHomeworkOpBO_Q(homeworkOpBO, new HashMap<>(2, 1f) {{
                put("srcQuestion", src);
                put("updateQuestion", getQR_DTO(src.getQuestion()));
            }});
        } catch (Exception ex) {
            logger.error("QuestionID: {} ?????? ???????????? ??????", qid);
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
                throw new Exception("???????????????/??????????????????");
            }
            Integer qtid = questionTypeDao.selectByName(type);
            if (qtid <= 0) {
                throw new Exception("?????????????????????");
            }
            Question srcQuestion = selectHKByQID(qid).getQuestion();
            if (srcQuestion == null) {
                throw new Exception("???????????????");
            }
            if (teacherQuestionDao.accurateSelect(new TeacherQuestion(tid, qid)) <= 0) {
                throw new Exception("??????????????????");
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
            logger.info("QuestionID: {} ????????????", qid);
            logger.info("Question ????????? {} ?????????", update);
            logger.info("Question_Type ????????? {} ?????????", update);
            setHomeworkOpBO_Q(homeworkOpBO, new HashMap<>(2, 1f) {{
                put("srcQuestion", qr_dto);
                put("updateQuestion", getQR_DTO(question));
            }});
        } catch (Exception ex) {
            logger.error("QuestionID: {} ????????????", qid);
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
                throw new Exception("???????????????");
            }
            if (!result.getIsCheck()) {
                throw new Exception("?????????");
            }
            if (!userService.isTeacher(tid)) {
                throw new Exception("???????????????/??????????????????");
            }
            if (teacherQuestionDao.accurateSelect(new TeacherQuestion(tid, qid)) <= 0) {
                throw new Exception("??????????????????");
            }
            if (questionResultDao.accurateSelect(new QuestionResult(qid, rid)) <= 0) {
                throw new Exception("?????????????????????");
            }
            flag = 1;
            int update = resultDao.updateResult(result);
            flag = 2;
            logger.info("TeacherID: {} ?????? QuestionID: {} ResultID: {} ??????", tid, qid, rid);
            logger.info("Result ?????? {} ?????????", update);
            setHomeworkOpBO_R(homeworkOpBO, new HashMap<>(2, 1f) {{
                put("srcResult", getRQ_DTO(srcResult));
                put("updateResult", getRQ_DTO(result));
            }});
        } catch (Exception ex) {
            logger.error("TeacherID: {} ?????? QuestionID: {} ResultID{} ??????", tid, qid, rid);
            switch (flag) {
                case 0 -> logger.error("????????????: {}", ex.getMessage());
                case 1 -> logger.error("Result ????????????: {}", ex.getMessage());
                default -> logger.error("????????????: {}", ex.getMessage());
            }
            if (flag == 1) {
                throw new SQLRWException("Teacher ?????? Result ????????????");
            }
            setHomeworkOpBO_R(homeworkOpBO,
                    flag == 2 ? "Result ????????????, ???????????????" : "Result ????????????",
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
            logger.error("UserID: {} ???????????????/??????", uid);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = SQLWarningException.class)
    public boolean deleteType(Integer uid, String typeName) {
        if (userService.isAdmin(uid) || userService.isTeacher(uid)) {
            return questionTypeDao.deleteQuestionType(typeName) > 0;
        } else {
            logger.error("UserID: {} ???????????????/??????", uid);
            return false;
        }
    }

    @Override
    @Transactional(rollbackFor = SQLWarningException.class)
    public boolean deleteType(Integer uid, Integer id) {
        if (userService.isAdmin(uid) || userService.isTeacher(uid)) {
            return questionTypeDao.deleteByID(id) > 0;
        } else {
            logger.error("UserID: {} ???????????????/??????", uid);
            return false;
        }
    }

    /*
    ??????
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
                throw new Exception("???????????????/??????????????????");
            }
            if (!courseService.isLearningByStudent(cid, sid)) {
                throw new Exception("????????????????????????");
            }
            if (questionCourseDao.accurateDelete(new QuestionCourse(qid, cid)) <= 0) {
                throw new Exception("???????????????????????????");
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
            logger.info("StudentID: {} ??????????????????", sid);
            logger.info("Result ?????? {} ?????????", insert[0]);
            logger.info("QuestionResult ?????? {} ?????????", insert[1]);
            logger.info("ResultCourse ?????? {} ?????????", insert[2]);
            logger.info("StudentResult ?????? {} ?????????", insert[3]);
            setHomeworkOpBO_R(homeworkOpBO, new HashMap<>(1, 1f) {{
                put("??????Result", getRQ_DTO(finalResult));
            }});
        } catch (Exception ex) {
            logger.error("UserID: {} ??????????????????", sid);
            switch (flag) {
                case 0 -> logger.error("????????????: {}", ex.getMessage());
                case 1 -> logger.error("Result ????????????: {}", ex.getMessage());
                case 2 -> logger.error("QuestionResult ????????????: {}", ex.getMessage());
                case 3 -> logger.error("ResultCourse ????????????: {}", ex.getMessage());
                case 4 -> logger.error("StudentResult ????????????: {}", ex.getMessage());
                default -> logger.error("????????????: {}", ex.getMessage());
            }
            if (flag >= 1 && flag <= 4) {
                throw new SQLRWException("?????? Result ??????");
            }
            setHomeworkOpBO_R(homeworkOpBO,
                    flag == 5 ? "?????? Result ??????, ???????????????" : "?????? Result ??????",
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
                throw new Exception("???????????????/??????????????????");
            }
            Result srcResult = resultDao.selectByID(rid);
            if (srcResult == null) {
                throw new Exception("???????????????");
            }
            if (studentResultDao.accurateSelect(new StudentResult(sid, rid)) <= 0) {
                throw new Exception("??????????????????");
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
            logger.info("StudentID: {} ??????????????????", sid);
            logger.info("Result ?????? {} ?????????", delete[0]);
            logger.info("QuestionResult ?????? {} ?????????", delete[1]);
            logger.info("ResultCourse ?????? {} ?????????", delete[2]);
            logger.info("StudentResult ?????? {} ?????????", delete[3]);
            setHomeworkOpBO_R(homeworkOpBO, new HashMap<>(1, 1f) {{
                put("srcResult", getRQ_DTO(srcResult));
            }});
        } catch (Exception ex) {
            logger.error("UserID: {} ??????????????????", sid);
            switch (flag) {
                case 0 -> logger.error("????????????: {}", ex.getMessage());
                case 4 -> logger.error("Result ????????????: {}", ex.getMessage());
                case 3 -> logger.error("QuestionResult ????????????: {}", ex.getMessage());
                case 2 -> logger.error("ResultCourse ????????????: {}", ex.getMessage());
                case 1 -> logger.error("StudentResult ????????????: {}", ex.getMessage());
                default -> logger.error("????????????: {}", ex.getMessage());
            }
            if (flag >= 1 && flag <= 4) {
                throw new SQLRWException("?????? Result ??????");
            }
            setHomeworkOpBO_R(homeworkOpBO,
                    flag == 5 ? "?????? Result ??????, ???????????????" : "?????? Result ??????",
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
                throw new Exception("???????????????/??????????????????");
            }
            Result srcResult = resultDao.selectByID(rid);
            if (srcResult == null) {
                throw new Exception("???????????????");
            }
            if (studentResultDao.accurateSelect(new StudentResult(sid, rid)) <= 0) {
                throw new Exception("??????????????????");
            }
            ResultQuestionDTO rq_dto = getRQ_DTO(srcResult);
            flag = 1;
            int update = resultDao.updateResult(result);
            flag = 2;
            logger.info("StudentID: {} ??????????????????", sid);
            logger.info("Result ?????? {} ?????????", update);
            setHomeworkOpBO_R(homeworkOpBO, new HashMap<>(2, 1f) {{
                put("srcResult", rq_dto);
                put("updateResult", getRQ_DTO(result));
            }});
        } catch (Exception ex) {
            logger.error("UserID: {} ??????????????????", sid);
            switch (flag) {
                case 0 -> logger.error("????????????: {}", ex.getMessage());
                case 1 -> logger.error("Result ????????????: {}", ex.getMessage());
                default -> logger.error("????????????: {}", ex.getMessage());
            }
            if (flag == 1) {
                throw new SQLRWException("?????? Result ??????");
            }
            setHomeworkOpBO_R(homeworkOpBO,
                    flag == 2 ? "?????? Result ??????, ???????????????" : "?????? Result ??????",
                    ex.getMessage());
        }
        return homeworkOpBO;
    }
}
