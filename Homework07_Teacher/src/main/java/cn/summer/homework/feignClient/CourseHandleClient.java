package cn.summer.homework.feignClient;

import cn.summer.homework.BO.CourseOpBO;
import cn.summer.homework.DTO.NewCourseDTO;
import cn.summer.homework.PO.TeacherCourse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author VHBin
 * @date 2022/8/7-15:17
 */

@FeignClient("CourseManagerService")
public interface CourseHandleClient {
    @PostMapping("/course-handle/teacher/new")
    CourseOpBO create(@RequestBody NewCourseDTO newCourse);

    @PostMapping("/course-handle/teacher/add")
    CourseOpBO add(@RequestBody TeacherCourse tc);

    @PostMapping("/course-handle/teacher/drop")
    CourseOpBO drop(@RequestBody TeacherCourse tc);

    @PostMapping("/course-handle/teacher/update")
    CourseOpBO update(@RequestBody NewCourseDTO updateCourse);

    @PostMapping("/course-handle/teacher/delete")
    CourseOpBO delete(@RequestBody TeacherCourse tc);
}
