package cn.summer.homework.service;

import cn.summer.homework.BO.CourseOpBO;
import cn.summer.homework.DTO.NewCourseDTO;
import cn.summer.homework.PO.TeacherCourse;

/**
 * @author VHBin
 * @date 2022/7/15-18:28
 */
public interface CourseTeacherService {
    CourseOpBO createCourse(NewCourseDTO newCourse);

    CourseOpBO addTeacher(TeacherCourse tc);

    CourseOpBO dropTeacher(TeacherCourse tc);

    CourseOpBO update(NewCourseDTO updateCourse);

    CourseOpBO delete(TeacherCourse tc);
}
