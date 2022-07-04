package cn.summer.homework.service.impl;

import cn.summer.homework.DTO.CourseInfo;
import cn.summer.homework.dao.CourseDao;
import cn.summer.homework.dao.cascade.StudentCourseDao;
import cn.summer.homework.dao.cascade.TeacherCourseDao;
import cn.summer.homework.service.CourseService;
import cn.summer.homework.service.UserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/7/4-15:08
 */

@Service
public class CourseServiceImpl implements CourseService {
    @Resource
    private CourseDao courseDao;
    @Resource
    private UserService userService;
    @Resource
    private StudentCourseDao studentCourseDao;
    @Resource
    private TeacherCourseDao teacherCourseDao;

    @Override
    public CourseInfo getAllCourse() {
        return null;
    }
}
