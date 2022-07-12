package cn.summer.homework.controller;

import cn.summer.homework.DTO.CourseSTDTO;
import cn.summer.homework.service.CourseService;
import cn.summer.homework.service.HomeworkService;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/12-13:53
 */

@RefreshScope
@RestController
@RequestMapping("course")
public class CourseController {
    @Resource
    private CourseService courseService;
    @Resource
    private HomeworkService homeworkService;

    @GetMapping("courses-get")
    public List<CourseSTDTO> getAll() {
        return courseService.getAllCourse();
    }

    @GetMapping("course-get")
    public CourseSTDTO get(@RequestParam("id") Integer id) {
        return courseService.getCourse(id);
    }

    @GetMapping("courses-get-name")
    public List<CourseSTDTO> getByName(@RequestParam("name") String name) {
        return courseService.getCourse(name);
    }
}
