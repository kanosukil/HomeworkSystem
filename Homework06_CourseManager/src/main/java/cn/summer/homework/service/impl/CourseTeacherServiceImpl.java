package cn.summer.homework.service.impl;

import cn.summer.homework.BO.CourseOpBO;
import cn.summer.homework.DTO.NewCourseDTO;
import cn.summer.homework.PO.TeacherCourse;
import cn.summer.homework.feignClient.CourseClient;
import cn.summer.homework.service.CourseTeacherService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/8/6-23:59
 */

@Service
public class CourseTeacherServiceImpl implements CourseTeacherService {
    @Resource
    private CourseClient client;

    @Override
    public CourseOpBO createCourse(NewCourseDTO newCourse) {
        return null;
    }

    @Override
    public CourseOpBO addTeacher(TeacherCourse tc) {
        return null;
    }

    @Override
    public CourseOpBO dropTeacher(TeacherCourse tc) {
        return null;
    }

    @Override
    public CourseOpBO update(NewCourseDTO updateCourse) {
        return null;
    }

    @Override
    public CourseOpBO delete(TeacherCourse tc) {
        return null;
    }
}
