package cn.summer.homework.controller;

import cn.summer.homework.BO.CourseOpBO;
import cn.summer.homework.DTO.CourseInDTO;
import cn.summer.homework.PO.StudentCourse;
import cn.summer.homework.VO.StudentVO;
import cn.summer.homework.feignClient.CourseHandleClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @author VHBin
 * @date 2022/8/8-16:52
 */

@RestController
@RequestMapping("/s/ao")
public class AddOpController {
    @Resource
    private CourseHandleClient course;

    @PostMapping("/add/course")
    public StudentVO addCourse(@RequestBody CourseInDTO in) {
        CourseOpBO res = course.addS(new StudentCourse(in.getSid(), in.getCid()));
        Map<String, Object> map = res.getMap();
        if (res.getIsSuccess()) {
            return new StudentVO(200, "course", map.get("Course").toString());
        } else {
            return new StudentVO(500, "Cause", map.get("Cause").toString());
        }
    }

    @PostMapping("/drop/course")
    public StudentVO dropCourse(@RequestBody CourseInDTO in) {
        CourseOpBO res = course.dropS(new StudentCourse(in.getSid(), in.getCid()));
        Map<String, Object> map = res.getMap();
        if (res.getIsSuccess()) {
            return new StudentVO(200, "course", map.get("Course").toString());
        } else {
            return new StudentVO(500, "Cause", map.get("Cause").toString());
        }
    }
}
