package cn.summer.homework.controller;

import cn.summer.homework.BO.ESOpBO;
import cn.summer.homework.DTO.*;
import cn.summer.homework.Entity.Result;
import cn.summer.homework.Util.IndexUtil;
import cn.summer.homework.VO.StudentVO;
import cn.summer.homework.feignClient.ESCRUDClient;
import cn.summer.homework.feignClient.StudentClient;
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
@RequestMapping("/operation/student")
public class StudentController {
    private static final Logger logger = LoggerFactory.getLogger(StudentController.class);
    @Resource
    private ElasticSearchDirectExchangeService es;
    @Resource
    private StudentClient student;
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

    @PostMapping("/c/result")
    public StudentVO createResult(@RequestBody ResultInDTO in) {
        try {
            StudentVO result = student.createResult(in);
            if (result.getCode() == 200) {
                ResultQuestionDTO rInSQL = find.result(Integer.parseInt(result.getInfo()));
                if (es.save(rInSQL)) {
                    logger.info("<result>MQ ES Save 成功");
                } else {
                    logger.warn("<result>MQ ES Save 异常, 未存入 ES 中");
                    esIndexDelete(IndexUtil.RESULT);
                }
            }
            return result;
        } catch (IOException io) {
            logger.error("[Create Result]SQL Result 获取异常", io);
            return new StudentVO(500, "CreateResult: SQL Result 获取异常", io.toString());
        }
    }

    @PostMapping("/u/result")
    public StudentVO updateResult(@RequestBody ResultInDTO in) {
        try {
            StudentVO result = student.updateResult(in);
            if (result.getCode() == 200) {
                ResultQuestionDTO after = find.result(in.getResult().getId());
                if (es.update(after)) {
                    logger.info("<result>MQ ES Update 成功");
                } else {
                    logger.warn("<result>MQ ES Update 异常, 未更新 ES");
                    esIndexDelete(IndexUtil.RESULT);
                }
            }
            return result;
        } catch (IOException io) {
            logger.error("[Update Result]SQL Result 获取异常", io);
            return new StudentVO(500, "UpdateResult: SQL Result 获取异常", io.toString());
        }
    }

    @PostMapping("/d/result")
    public StudentVO deleteResult(@RequestBody ResultInDTO in) {
        StudentVO result = student.deleteResult(in);
        if (result.getCode() == 200) {
            Result before = new Result();
            before.setId(in.getRid());
            if (es.delete(new ResultQuestionDTO(before,
                    null, null))) {
                logger.info("<result>MQ ES Delete 成功");
            } else {
                logger.warn("<result>MQ ES Delete 异常, 未删除指定 ES 文档");
                esIndexDelete(IndexUtil.RESULT);
            }
        }
        return result;
    }

    @PostMapping("/ao/add/course")
    public StudentVO addCourse(@RequestBody CourseInDTO in) {
        try {
            StudentVO course = student.addCourse(in);
            if (course.getCode() == 200) {
                CourseSTDTO after = find.course(in.getCid());
                if (es.update(after)) {
                    logger.info("<course-sadd>MQ ES Update 成功");
                } else {
                    logger.warn("<course-sadd>MQ ES Update 异常, 未更新 ES");
                    esIndexDelete(IndexUtil.COURSE);
                }
            }
            return course;
        } catch (IOException io) {
            logger.error("[Add Course]SQL Course 获取异常", io);
            return new StudentVO(500, "AddCourse: SQL Course 获取异常", io.toString());
        }
    }

    @PostMapping("/ao/drop/course")
    public StudentVO dropCourse(@RequestBody CourseInDTO in) {
        try {
            StudentVO course = student.dropCourse(in);
            if (course.getCode() == 200) {
                CourseSTDTO after = find.course(in.getCid());
                if (es.update(after)) {
                    logger.info("<course-sdrop>MQ ES Update 成功");
                } else {
                    logger.warn("<course-sdrop>MQ ES Update 异常, 未更新 ES");
                    esIndexDelete(IndexUtil.COURSE);
                }
            }
            return course;
        } catch (IOException io) {
            logger.error("[Drop Course]SQL Course 获取异常", io);
            return new StudentVO(500, "DropCourse: SQL Course 获取异常", io.toString());
        }
    }
}
