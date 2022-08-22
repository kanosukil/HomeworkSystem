package cn.summer.homework.controller;

import cn.summer.homework.BO.ESOpBO;
import cn.summer.homework.DTO.*;
import cn.summer.homework.Entity.Course;
import cn.summer.homework.Entity.Question;
import cn.summer.homework.Util.IndexUtil;
import cn.summer.homework.VO.TeacherVO;
import cn.summer.homework.feignClient.ESCRUDClient;
import cn.summer.homework.feignClient.TeacherClient;
import cn.summer.homework.service.ElasticSearchDirectExchangeService;
import cn.summer.homework.service.FindService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Objects;

/**
 * @author VHBin
 * @date 2022/8/7-16:59
 */

@RestController
@RequestMapping("/operation/teacher")
public class TeacherController {
    private static final Logger logger = LoggerFactory.getLogger(TeacherController.class);
    @Resource
    private ElasticSearchDirectExchangeService es;
    @Resource
    private TeacherClient teacher;
    @Resource
    private FindService find;
    @Resource
    private ESCRUDClient esCUD;

    private void esIndexDelete(String index) {
        ElasticSearchDTO dto = new ElasticSearchDTO();
        dto.setOption(4);
        dto.setIndex(index);
        ESOpBO esOpBO = esCUD.indexDelete(dto);
        if (!esOpBO.getIsSuccess()) {
            logger.warn("ES 删除目录{}失败", index);
        }
    }

    private void judge(Integer id, HttpServletRequest request)
            throws IOException {
        UserRoleDTO tmp = find.user(
                Integer.parseInt(request.getAttribute("userid").toString()));
        if (!tmp.getRoles().contains("Teacher")) {
            throw new IOException("无效操作: 非 Teacher");
        }
        if (!Objects.equals(id, tmp.getUser().getId())) {
            throw new IOException("无效操作: 不允许操作其他用户");
        }
    }

    /*
        course
     */

    /**
     * 新建课程
     *
     * @param in      tid+course
     * @param request 检测是否为本人操作
     * @return TeacherVO code+msg(cid)+info(cid值)
     */
    @PostMapping("/c/course")
    public TeacherVO createCourse(@RequestBody CourseInDTO in, HttpServletRequest request) {
        try {
            judge(in.getTid(), request);
            TeacherVO course = teacher.createCourse(in);
            if (course.getCode() == 200) {
                CourseSTDTO cInSQL = find.course(Integer.parseInt(course.getInfo()));
                if (!es.save(cInSQL)) {
                    logger.warn("<course>MQ ES Save 异常, 未存入 ES 中");
                    esIndexDelete(IndexUtil.COURSE);
                } else {
                    logger.info("<course>MQ ES Save 成功");
                }
            }
            return course;
        } catch (IOException io) {
            logger.error("[Create Course]SQL Course 获取异常", io);
            return new TeacherVO(500, "CreateCourse: SQL Course 获取异常", io.toString());
        }
    }

    /**
     * 更新课程
     *
     * @param in      tid+update
     * @param request 检测是否为本人操作
     * @return TeacherVO code+msg(course)+info(更新前的值)
     */
    @PostMapping("/u/course")
    public TeacherVO updateCourse(@RequestBody CourseInDTO in, HttpServletRequest request) {
        try {
            judge(in.getTid(), request);
            TeacherVO course = teacher.updateCourse(in);
            if (course.getCode() == 200) {
                CourseSTDTO after = find.course(in.getCourse().getId());
                if (!es.update(after)) {
                    logger.warn("<course>MQ ES Update 异常, 未更新 ES");
                    esIndexDelete(IndexUtil.COURSE);
                } else {
                    logger.info("<course>MQ ES Update 成功");
                }
            }
            return course;
        } catch (IOException io) {
            logger.error("[Update Course]SQL Course 获取异常", io);
            return new TeacherVO(500, "UpdateCourse: SQL Course 获取异常", io.toString());
        }
    }

    /**
     * 删除课程
     *
     * @param in      tid+cid
     * @param request 检测是否为本人
     * @return TeacherVO code+msg(course)+info(删除前的值)
     */
    @PostMapping("/d/course")
    public TeacherVO deleteCourse(@RequestBody CourseInDTO in, HttpServletRequest request) {
        try {
            judge(in.getTid(), request);
        } catch (IOException io) {
            logger.error("[Delete Course]", io);
            return new TeacherVO(400, "DeleteCourse: 无效操作", io.getMessage());
        }
        TeacherVO course = teacher.deleteCourse(in);
        if (course.getCode() == 200) {
            Course before = new Course();
            before.setId(in.getCid());
            if (es.delete(new CourseSTDTO(before, null, null))) {
                logger.info("<course>MQ ES Delete 成功");
                esIndexDelete(IndexUtil.QUESTION);
                esIndexDelete(IndexUtil.RESULT);
            } else {
                logger.warn("<course>MQ ES delete 异常, 未删除指定 ES 文档");
                esIndexDelete(IndexUtil.COURSE);
            }
        }
        return course;
    }

    /*
        question
     */

    /**
     * 新建问题
     *
     * @param in      tid+cid+question+type
     * @param request 检测是否为本人
     * @return TeacherVO code+msg(qid)+info(qid值)
     */
    @PostMapping("/c/question")
    public TeacherVO createQuestion(@RequestBody QuestionInDTO in, HttpServletRequest request) {
        try {
            judge(in.getTid(), request);
            TeacherVO question = teacher.createQuestion(in);
            if (question.getCode() == 200) {
                QuestionResultDTO qInSQL = find.question(Integer.parseInt(question.getInfo()));
                if (es.save(qInSQL)) {
                    logger.info("<question>MQ ES Save 成功");
                } else {
                    logger.warn("<question>MQ ES Save 异常, 未存入 ES 中");
                    esIndexDelete(IndexUtil.QUESTION);
                }
            }
            return question;
        } catch (IOException io) {
            logger.error("[Create Question]SQL Question 获取异常", io);
            return new TeacherVO(500, "CreateQuestion: SQL Question 获取异常", io.toString());
        }
    }

    /**
     * 更新问题
     *
     * @param in      tid+question 或 tid+qid+type 或 tid+question+type
     * @param request 检测是否为本人
     * @return TeacherVO code+msg(question)+info(更新后的值)
     */
    @PostMapping("/u/question")
    public TeacherVO updateQuestion(@RequestBody QuestionInDTO in, HttpServletRequest request) {
        try {
            judge(in.getTid(), request);
            TeacherVO question = teacher.updateQuestion(in);
            if (question.getCode() == 200) {
                QuestionResultDTO after = find.question(in.getQid());
                if (es.update(after)) {
                    logger.info("<question>MQ ES Update 成功");
                } else {
                    logger.warn("<question>MQ ES Update 异常, 未更新 ES");
                    esIndexDelete(IndexUtil.QUESTION);
                }
            }
            return question;
        } catch (IOException io) {
            logger.error("[Update Question]SQL Question 获取异常", io);
            return new TeacherVO(500, "UpdateQuestion: SQL Question 获取异常", io.toString());
        }
    }

    /**
     * 删除问题
     *
     * @param in      tid+qid
     * @param request 检测是否为本人
     * @return TeacherVO code+msg(question)+info(删除前的值)
     */
    @PostMapping("/d/question")
    public TeacherVO deleteQuestion(@RequestBody QuestionInDTO in, HttpServletRequest request) {
        try {
            judge(in.getTid(), request);
        } catch (IOException io) {
            logger.error("[Delete Question]", io);
            return new TeacherVO(400, "DeleteQuestion: 无效操作", io.getMessage());
        }
        TeacherVO question = teacher.deleteQuestion(in);
        if (question.getCode() == 200) {
            Question before = new Question();
            before.setId(in.getQid());
            if (es.delete(new QuestionResultDTO(before, null,
                    null, null, null))) {
                logger.info("<question>MQ ES Delete 成功");
                esIndexDelete(IndexUtil.RESULT);
            } else {
                logger.warn("<question>MQ ES delete 异常, 未删除指定 ES 文档");
                esIndexDelete(IndexUtil.QUESTION);
            }
        }
        return question;
    }

    /*
        type
     */

    /**
     * 新建题目类型
     *
     * @param in      tid+type
     * @param request 检测是否为本人
     * @return TeacherVO code+msg(type)+info(successful)
     */
    @PostMapping("/c/type")
    public TeacherVO createType(@RequestBody QuestionInDTO in, HttpServletRequest request) {
        try {
            judge(in.getTid(), request);
        } catch (IOException io) {
            logger.error("[Create Type]", io);
            return new TeacherVO(400, "CreateType: 无效操作", io.getMessage());
        }
        return teacher.createType(in);
    }

    /**
     * 删除题目类型
     *
     * @param in      tid+type
     * @param request 检测是否为本人
     * @return TeacherVO code+msg(type)+info(successful)
     */
    @PostMapping("/d/type")
    public TeacherVO deleteType(@RequestBody QuestionInDTO in, HttpServletRequest request) {
        try {
            judge(in.getTid(), request);
        } catch (IOException io) {
            logger.error("[Delete Type]", io);
            return new TeacherVO(400, "DeleteType: 无效操作", io.getMessage());
        }
        return teacher.deleteType(in);
    }

    /*
        AddOption
     */
    @PostMapping("/ao/correct/question")
    public TeacherVO correct(@RequestBody ResultInDTO in, HttpServletRequest request) {
        try {
            judge(in.getTid(), request);
            TeacherVO correct = teacher.correct(in);
            if (correct.getCode() == 200) {
                ResultQuestionDTO after = find.result(in.getResult().getId());
                if (es.update(after)) {
                    logger.info("<question-result>MQ ES Update 成功");
                } else {
                    logger.warn("<question-result>MQ ES Update 异常, 未更新 ES");
                    esIndexDelete(IndexUtil.RESULT);
                }
            }
            return correct;
        } catch (IOException io) {
            logger.error("[Correct Result]SQL Result 获取异常", io);
            return new TeacherVO(500, "CorrectQuestion: SQL Result 获取异常", io.toString());
        }
    }

    /**
     * 课程添加老师
     *
     * @param in      tid+cid
     * @param request 检测是否为本人
     * @return TeacherVO code+msg(course)+info(更新后的course值)
     */
    @PostMapping("/ao/add/course")
    public TeacherVO addCourse(@RequestBody CourseInDTO in, HttpServletRequest request) {
        try {
            judge(in.getTid(), request);
            TeacherVO course = teacher.addCourse(in);
            if (course.getCode() == 200) {
                CourseSTDTO after = find.course(in.getCid());
                if (es.update(after)) {
                    logger.info("<course-tadd>MQ ES Update 成功");
                } else {
                    logger.warn("<course-tadd>MQ ES Update 异常, 未更新 ES");
                    esIndexDelete(IndexUtil.COURSE);
                }
            }
            return course;
        } catch (IOException io) {
            logger.error("[Add Course]SQL Course 获取异常", io);
            return new TeacherVO(500, "AddCourse: SQL Course 获取异常", io.toString());
        }
    }

    /**
     * 老师从课程中退出
     *
     * @param in      tid+cid
     * @param request 检测是否为本人
     * @return TeacherVO code+msg(course)+info(更新后的course值)
     */
    @PostMapping("/ao/drop/course")
    public TeacherVO dropCourse(@RequestBody CourseInDTO in, HttpServletRequest request) {
        try {
            judge(in.getTid(), request);
            TeacherVO course = teacher.dropCourse(in);
            if (course.getCode() == 200) {
                CourseSTDTO after = find.course(in.getCid());
                if (es.update(after)) {
                    logger.info("<course-tdrop>MQ ES Update 成功");
                } else {
                    logger.warn("<course-tdrop>MQ ES Update 异常, 未更新 ES");
                    esIndexDelete(IndexUtil.COURSE);
                }
            }
            return course;
        } catch (IOException io) {
            logger.error("[Drop Course]SQL Course 获取异常", io);
            return new TeacherVO(500, "DropCourse: SQL Course 获取异常", io.toString());
        }
    }
}
