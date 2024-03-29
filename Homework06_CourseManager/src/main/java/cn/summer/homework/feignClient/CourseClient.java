package cn.summer.homework.feignClient;

import cn.summer.homework.BO.CourseOpBO;
import cn.summer.homework.DTO.CourseSTDTO;
import cn.summer.homework.DTO.NewCourseDTO;
import cn.summer.homework.PO.StudentCourse;
import cn.summer.homework.PO.TeacherCourse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/15-15:07
 */

@FeignClient("CourseSQL")
public interface CourseClient {
    @GetMapping("/course/courses-get")
    List<CourseSTDTO> getAll();

    @GetMapping("/course/course-get")
    CourseSTDTO get(@RequestParam("id") Integer id);

    @GetMapping("/course/courses-get-name")
    List<CourseSTDTO> getByName(@RequestParam("name") String name);

    @GetMapping("/course/courses-get-teacher")
    List<CourseSTDTO> getByTeacher(@RequestParam("tid") Integer tid);

    @GetMapping("/course/courses-get-student")
    List<CourseSTDTO> getByStudent(@RequestParam("sid") Integer sid);

    @PostMapping("/course/create-course")
    CourseOpBO createCourse(@RequestBody NewCourseDTO newCourse);

    @PostMapping("/course/course-add-student")
    CourseOpBO addStudent(@RequestBody StudentCourse sc);

    @PostMapping("/course/course-add-teacher")
    CourseOpBO addTeacher(@RequestBody TeacherCourse tc);

    @PostMapping("/course/course-drop-student")
    CourseOpBO dropStudent(@RequestBody StudentCourse sc);

    @PostMapping("/course/course-drop-teacher")
    CourseOpBO dropTeacher(@RequestBody TeacherCourse tc);

    @PostMapping("/course/course-update-name")
    CourseOpBO update(@RequestBody NewCourseDTO updateCourse);

    @PostMapping("/course/course-delete")
    CourseOpBO delete(@RequestBody TeacherCourse tc);
}
