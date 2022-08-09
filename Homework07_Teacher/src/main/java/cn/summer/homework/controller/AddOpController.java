package cn.summer.homework.controller;

import cn.summer.homework.BO.CourseOpBO;
import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.CourseInDTO;
import cn.summer.homework.DTO.NewResultDTO;
import cn.summer.homework.DTO.ResultInDTO;
import cn.summer.homework.PO.TeacherCourse;
import cn.summer.homework.VO.TeacherVO;
import cn.summer.homework.feignClient.CourseHandleClient;
import cn.summer.homework.feignClient.QuestionHandleClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author VHBin
 * @date 2022/8/8-15:59
 */

@RestController
@RequestMapping("/t/ao")
public class AddOpController {
    @Resource
    private CourseHandleClient course;
    @Resource
    private QuestionHandleClient question;

    @PostMapping("/correct/question")
    public TeacherVO correct(@RequestBody ResultInDTO in) {
        HomeworkOpBO res = question.correct(new NewResultDTO(
                in.getTid(),
                0,
                0,
                in.getResult()));
        if (res.getIsQuestion()) {
            return new TeacherVO(500, "批改回答异常", "问题标识:true");
        } else {
            Map<String, Object> map = res.getInfo();
            if (res.getIsSuccess()) {
                return new TeacherVO(200, "result", map.get("Result").toString());
            } else {
                return new TeacherVO(500, "Cause", map.get("Cause").toString());
            }
        }
    }

    @PostMapping("/add/course")
    public TeacherVO addCourse(@RequestBody CourseInDTO in) {
        CourseOpBO res = course.add(new TeacherCourse(in.getTid(), in.getCid()));
        Map<String, Object> map = res.getMap();
        if (res.getIsSuccess()) {
            return new TeacherVO(200, "course", map.get("Course").toString());
        } else {
            return new TeacherVO(500, "Cause", map.get("Cause").toString());
        }
    }

    @PostMapping("/drop/course")
    public TeacherVO dropCourse(@RequestBody CourseInDTO in) {
        CourseOpBO res = course.drop(new TeacherCourse(in.getTid(), in.getCid()));
        Map<String, Object> map = res.getMap();
        if (res.getIsSuccess()) {
            return new TeacherVO(200, "course", map.get("Course").toString());
        } else {
            return new TeacherVO(500, "Cause", map.get("Cause").toString());
        }
    }
}
