package cn.summer.homework.controller;

import cn.summer.homework.BO.ESOpBO;
import cn.summer.homework.DTO.*;
import cn.summer.homework.Entity.Course;
import cn.summer.homework.Entity.Question;
import cn.summer.homework.Entity.Result;
import cn.summer.homework.Util.IndexUtil;
import cn.summer.homework.VO.AdminVO;
import cn.summer.homework.feignClient.AdminClient;
import cn.summer.homework.feignClient.ESCRUDClient;
import cn.summer.homework.service.ElasticSearchDirectExchangeService;
import cn.summer.homework.service.FindService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author VHBin
 * @date 2022/8/7-17:00
 */

@RestController
@RequestMapping("/operation/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
    @Resource
    private AdminClient admin;
    @Resource
    private FindService find;
    @Resource
    private ESCRUDClient esCUD;
    @Resource
    private ElasticSearchDirectExchangeService es;

    private void esIndexDelete(String index) {
        ElasticSearchDTO dto = new ElasticSearchDTO();
        dto.setOption(4);
        dto.setIndex(index);
        ESOpBO esOpBO = esCUD.indexDelete(dto);
        if (!esOpBO.getIsSuccess()) {
            logger.warn("ad: ES 删除目录{}失败", index);
        }
    }

    /*
        result
     */
    @PostMapping("/c/result")
    public AdminVO createResult(@RequestBody ResultInDTO in) {
        logger.info("Administer Operation");
        try {
            AdminVO result = admin.createResult(in);
            if (result.getCode() == 200) {
                ResultQuestionDTO rInSQL = find.result(Integer.parseInt(result.getInfo()));
                if (es.save(rInSQL)) {
                    logger.info("<ad-result>MQ ES Save 成功");
                } else {
                    logger.warn("<ad-result>MQ ES Save 异常, 未存入 ES 中");
                    esIndexDelete(IndexUtil.RESULT);
                }
            }
            return result;
        } catch (IOException io) {
            logger.error("ad:[Create Result]SQL Result 获取异常", io);
            return new AdminVO(500, "CreateResult: SQL Result 获取异常", io.toString());
        }
    }

    @PostMapping("/u/result")
    public AdminVO updateResult(@RequestBody ResultInDTO in) {
        logger.info("Administer Operation");
        try {
            AdminVO result = admin.updateResult(in);
            if (result.getCode() == 200) {
                ResultQuestionDTO after = find.result(in.getResult().getId());
                if (es.update(after)) {
                    logger.info("<ad-result>MQ ES Update 成功");
                } else {
                    logger.warn("<ad-result>MQ ES Update 异常, 未更新 ES");
                    esIndexDelete(IndexUtil.RESULT);
                }
            }
            return result;
        } catch (IOException io) {
            logger.error("ad:[Update Result]SQL Result 获取异常", io);
            return new AdminVO(500, "UpdateResult: SQL Result 获取异常", io.toString());
        }
    }

    @PostMapping("/d/result")
    public AdminVO deleteResult(@RequestBody ResultInDTO in) {
        logger.info("Administer Operation");
        AdminVO result = admin.deleteResult(in);
        if (result.getCode() == 200) {
            Result before = new Result();
            before.setId(in.getRid());
            if (es.delete(new ResultQuestionDTO(before,
                    null, null))) {
                logger.info("<ad-result>MQ ES Delete 成功");
            } else {
                logger.warn("<ad-result>MQ ES Delete 异常, 未删除指定 ES 文档");
                esIndexDelete(IndexUtil.RESULT);
            }
        }
        return result;
    }

    /*
        course
     */
    @PostMapping("/c/course")
    public AdminVO createCourse(@RequestBody CourseInDTO in) {
        logger.info("Administer Operation");
        try {
            AdminVO course = admin.createCourse(in);
            if (course.getCode() == 200) {
                CourseSTDTO cInSQL = find.course(Integer.parseInt(course.getInfo()));
                if (!es.save(cInSQL)) {
                    logger.warn("<ad-course>MQ ES Save 异常, 未存入 ES 中");
                    esIndexDelete(IndexUtil.COURSE);
                } else {
                    logger.info("<ad-course>MQ ES Save 成功");
                }
            }
            return course;
        } catch (IOException io) {
            logger.error("ad:[Create Course]SQL Course 获取异常", io);
            return new AdminVO(500, "CreateCourse: SQL Course 获取异常", io.toString());
        }
    }

    @PostMapping("/u/course")
    public AdminVO updateCourse(@RequestBody CourseInDTO in) {
        logger.info("Administer Operation");
        try {
            AdminVO course = admin.updateCourse(in);
            if (course.getCode() == 200) {
                CourseSTDTO after = find.course(in.getCourse().getId());
                if (!es.update(after)) {
                    logger.warn("<ad-course>MQ ES Update 异常, 未更新 ES");
                    esIndexDelete(IndexUtil.COURSE);
                } else {
                    logger.info("<ad-course>MQ ES Update 成功");
                }
            }
            return course;
        } catch (IOException io) {
            logger.error("ad:[Update Course]SQL Course 获取异常", io);
            return new AdminVO(500, "UpdateCourse: SQL Course 获取异常", io.toString());
        }
    }

    @PostMapping("/d/course")
    public AdminVO deleteCourse(@RequestBody CourseInDTO in) {
        logger.info("Administer Operation");
        AdminVO course = admin.deleteCourse(in);
        if (course.getCode() == 200) {
            Course before = new Course();
            before.setId(in.getCid());
            if (es.delete(new CourseSTDTO(before, null, null))) {
                logger.info("<ad-course>MQ ES Delete 成功");
            } else {
                logger.warn("<ad-course>MQ ES delete 异常, 未删除指定 ES 文档");
                esIndexDelete(IndexUtil.COURSE);
            }
        }
        return course;
    }

    /*
        question
     */
    @PostMapping("/c/question")
    public AdminVO createQuestion(@RequestBody QuestionInDTO in) {
        logger.info("Administer Operation");
        try {
            AdminVO question = admin.createQuestion(in);
            if (question.getCode() == 200) {
                QuestionResultDTO qInSQL = find.question(Integer.parseInt(question.getInfo()));
                if (es.save(qInSQL)) {
                    logger.info("<ad-question>MQ ES Save 成功");
                } else {
                    logger.warn("<ad-question>MQ ES Save 异常, 未存入 ES 中");
                    esIndexDelete(IndexUtil.QUESTION);
                }
            }
            return question;
        } catch (IOException io) {
            logger.error("ad:[Create Question]SQL Question 获取异常", io);
            return new AdminVO(500, "CreateQuestion: SQL Question 获取异常", io.toString());
        }
    }

    @PostMapping("/u/question")
    public AdminVO updateQuestion(@RequestBody QuestionInDTO in) {
        logger.info("Administer Operation");
        try {
            AdminVO question = admin.updateQuestion(in);
            if (question.getCode() == 200) {
                QuestionResultDTO after = find.question(in.getQid());
                if (es.update(after)) {
                    logger.info("<ad-question>MQ ES Update 成功");
                } else {
                    logger.warn("<ad-question>MQ ES Update 异常, 未更新 ES");
                    esIndexDelete(IndexUtil.QUESTION);
                }
            }
            return question;
        } catch (IOException io) {
            logger.error("ad:[Update Question]SQL Question 获取异常", io);
            return new AdminVO(500, "UpdateQuestion: SQL Question 获取异常", io.toString());
        }
    }

    @PostMapping("/d/question")
    public AdminVO deleteQuestion(@RequestBody QuestionInDTO in) {
        logger.info("Administer Operation");
        AdminVO question = admin.deleteQuestion(in);
        if (question.getCode() == 200) {
            Question before = new Question();
            before.setId(in.getQid());
            if (es.delete(new QuestionResultDTO(before,
                    null, null, null))) {
                logger.info("<ad-question>MQ ES Delete 成功");
            } else {
                logger.warn("<ad-question>MQ ES delete 异常, 未删除指定 ES 文档");
                esIndexDelete(IndexUtil.QUESTION);
            }
        }
        return question;
    }

    /*
        type
     */
    @PostMapping("/c/type")
    public AdminVO createType(@RequestBody QuestionInDTO in) {
        logger.info("Administer Operation");
        return admin.createType(in);
    }

    @PostMapping("/d/type")
    public AdminVO deleteType(@RequestBody QuestionInDTO in) {
        logger.info("Administer Operation");
        return admin.deleteType(in);
    }
}
