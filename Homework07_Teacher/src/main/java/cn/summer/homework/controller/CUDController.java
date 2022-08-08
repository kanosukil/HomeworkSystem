package cn.summer.homework.controller;

import cn.summer.homework.BO.CourseOpBO;
import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.CourseInDTO;
import cn.summer.homework.DTO.NewCourseDTO;
import cn.summer.homework.DTO.NewQuestionDTO;
import cn.summer.homework.DTO.QuestionInDTO;
import cn.summer.homework.PO.TeacherCourse;
import cn.summer.homework.PO.TeacherQuestion;
import cn.summer.homework.VO.TeacherVO;
import cn.summer.homework.feignClient.CourseHandleClient;
import cn.summer.homework.feignClient.QuestionHandleClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

/**
 * @author VHBin
 * @date 2022/8/8-14:21
 */

@RestController
@RequestMapping("t")
public class CUDController {
    private static final Logger logger = LoggerFactory.getLogger(CUDController.class);
    @Resource
    private CourseHandleClient course;
    @Resource
    private QuestionHandleClient question;

    private TeacherVO getCause(Map<String, Object> map) {
        return new TeacherVO(500, "Cause", map.get("Cause").toString());
    }

    @PostMapping("/c/course")
    public TeacherVO createCourse(@RequestBody CourseInDTO in) {
        CourseOpBO res = course.create(new NewCourseDTO(in.getCourse(), in.getTid()));
        Map<String, Object> map = res.getMap();
        if (res.getIsSuccess()) {
            return new TeacherVO(200, "cid", map.get("cid").toString());
        } else {
            return getCause(map);
        }
    }

    @PostMapping("/u/course")
    public TeacherVO updateCourse(@RequestBody CourseInDTO in) {
        CourseOpBO res = course.update(new NewCourseDTO(in.getCourse(), in.getTid()));
        Map<String, Object> map = res.getMap();
        if (res.getIsSuccess()) {
            return new TeacherVO(200, "course", map.get("Course").toString());
        } else {
            return getCause(map);
        }
    }

    @PostMapping("/d/course")
    public TeacherVO deleteCourse(@RequestBody CourseInDTO in) {
        CourseOpBO res = course.delete(new TeacherCourse(in.getTid(), in.getCid()));
        Map<String, Object> map = res.getMap();
        if (res.getIsSuccess()) {
            return new TeacherVO(200, "course", map.get("Course").toString());
        } else {
            return getCause(map);
        }
    }

    @PostMapping("/c/question")
    public TeacherVO createQuestion(@RequestBody QuestionInDTO in) {
        HomeworkOpBO res = question.createNew(new NewQuestionDTO(
                in.getTid(),
                in.getCid(),
                in.getQuestion(),
                in.getType()));
        if (res.getIsQuestion()) {
            Map<String, Object> map = res.getInfo();
            if (res.getIsSuccess()) {
                return new TeacherVO(200, "qid", map.get("qid").toString());
            } else {
                return getCause(map);
            }
        } else {
            return new TeacherVO(500, "创建问题异常", "问题标识:false");
        }
    }

    @PostMapping("/u/question")
    public TeacherVO updateQuestion(@RequestBody QuestionInDTO in) {
        HomeworkOpBO res = question.update(new NewQuestionDTO(
                in.getTid(),
                in.getQid(),
                in.getQuestion(),
                in.getType()));
        if (res.getIsQuestion()) {
            Map<String, Object> map = res.getInfo();
            if (res.getIsSuccess()) {
                return new TeacherVO(200, "question", map.get("Question").toString());
            } else {
                return getCause(map);
            }
        } else {
            return new TeacherVO(500, "更新问题异常", "问题标识:false");
        }
    }

    @PostMapping("/d/question")
    public TeacherVO deleteQuestion(@RequestBody QuestionInDTO in) {
        HomeworkOpBO res = question.delete(new TeacherQuestion(in.getTid(), in.getQid()));
        if (res.getIsQuestion()) {
            Map<String, Object> map = res.getInfo();
            if (res.getIsSuccess()) {
                return new TeacherVO(200, "question", map.get("Question").toString());
            } else {
                return getCause(map);
            }
        } else {
            return new TeacherVO(500, "删除问题异常", "问题标识:false");
        }
    }

    @PostMapping("/c/type")
    public TeacherVO createType(@RequestBody QuestionInDTO in) {
        try {
            if (question.createT(new NewQuestionDTO(
                    in.getTid(), 0, null, in.getType()))) {
                return new TeacherVO(200, "type", "successful");
            } else {
                return new TeacherVO(200, "type", "fail");
            }
        } catch (IOException io) {
            logger.error("新建问题类型异常", io);
            return new TeacherVO(500, "新建问题类型异常", io.toString());
        }
    }

    @PostMapping("/d/type")
    public TeacherVO deleteType(@RequestBody QuestionInDTO in) {
        try {
            if (question.deleteT(new NewQuestionDTO(
                    in.getTid(), in.getTypeid(), null, in.getType()))) {
                return new TeacherVO(200, "type", "successful");
            } else {
                return new TeacherVO(200, "type", "fail");
            }
        } catch (IOException io) {
            logger.error("删除问题类型异常", io);
            return new TeacherVO(500, "删除问题类型异常", io.toString());
        }
    }
}
