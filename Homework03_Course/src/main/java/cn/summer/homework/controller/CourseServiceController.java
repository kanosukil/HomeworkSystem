package cn.summer.homework.controller;

import cn.summer.homework.BO.CourseOpBO;
import cn.summer.homework.DTO.CourseSTDTO;
import cn.summer.homework.DTO.NewCourseDTO;
import cn.summer.homework.PO.StudentCourse;
import cn.summer.homework.PO.TeacherCourse;
import cn.summer.homework.feignClient.CourseSQLClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/15-0:01
 */

@RefreshScope
@RestController
@RequestMapping("course")
public class CourseServiceController {
    @Resource
    private CourseSQLClient sqlClient;

    @GetMapping("courses-get")
    public List<CourseSTDTO> getAll() {
        return sqlClient.getAll();
    }

    @GetMapping("course-get")
    public CourseSTDTO get(@RequestParam("id") Integer id) {
        return sqlClient.get(id);
    }

    @GetMapping("courses-get-name")
    public List<CourseSTDTO> getByName(@RequestParam("name") String name) {
        return sqlClient.getByName(name);
    }

    @PostMapping("create-course")
    public CourseOpBO createCourse(@RequestBody NewCourseDTO newCourse) {
        return sqlClient.createCourse(newCourse);
    }

    @PostMapping("course-add-student")
    public CourseOpBO addStudent(@RequestBody StudentCourse sc) {
        return sqlClient.addStudent(sc);
    }

    @PostMapping("course-add-teacher")
    public CourseOpBO addTeacher(@RequestBody TeacherCourse tc) {
        return sqlClient.addTeacher(tc);
    }

    @PostMapping("course-drop-student")
    public CourseOpBO dropStudent(@RequestBody StudentCourse sc) {
        return sqlClient.dropStudent(sc);
    }

    @PostMapping("course-drop-teacher")
    public CourseOpBO dropTeacher(@RequestBody TeacherCourse tc) {
        return sqlClient.dropTeacher(tc);
    }

    @PostMapping("course-update-name")
    public CourseOpBO update(@RequestBody NewCourseDTO updateCourse) {
        return sqlClient.update(updateCourse);
    }

    @PostMapping("course-delete")
    public CourseOpBO delete(@RequestBody TeacherCourse tc) {
        return sqlClient.delete(tc);
    }
}
