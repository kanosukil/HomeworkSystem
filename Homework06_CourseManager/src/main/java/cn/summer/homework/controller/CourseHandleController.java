package cn.summer.homework.controller;

import cn.summer.homework.BO.CourseOpBO;
import cn.summer.homework.DTO.NewCourseDTO;
import cn.summer.homework.PO.StudentCourse;
import cn.summer.homework.PO.TeacherCourse;
import cn.summer.homework.service.CourseStudentService;
import cn.summer.homework.service.CourseTeacherService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/8/6-22:21
 */

@RestController
@RequestMapping("course-handle")
public class CourseHandleController {
    @Resource
    private CourseStudentService student;
    @Resource
    private CourseTeacherService teacher;

    @PostMapping("/student/drop")
    public CourseOpBO dropS(@RequestBody StudentCourse sc) {
        return student.dropStudent(sc);
    }

    @PostMapping("/student/add")
    public CourseOpBO addS(@RequestBody StudentCourse sc) {
        return student.addStudent(sc);
    }

    @PostMapping("/teacher/new")
    public CourseOpBO create(@RequestBody NewCourseDTO newCourse) {
        return teacher.createCourse(newCourse);
    }

    @PostMapping("/teacher/add")
    public CourseOpBO add(@RequestBody TeacherCourse tc) {
        return teacher.addTeacher(tc);
    }

    @PostMapping("/teacher/drop")
    public CourseOpBO drop(@RequestBody TeacherCourse tc) {
        return teacher.dropTeacher(tc);
    }

    @PostMapping("/teacher/update")
    public CourseOpBO update(@RequestBody NewCourseDTO updateCourse) {
        return teacher.update(updateCourse);
    }

    @PostMapping("/teacher/delete")
    public CourseOpBO delete(@RequestBody TeacherCourse tc) {
        return teacher.delete(tc);
    }
}
