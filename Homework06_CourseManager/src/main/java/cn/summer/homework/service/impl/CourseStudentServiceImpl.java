package cn.summer.homework.service.impl;

import cn.summer.homework.BO.CourseOpBO;
import cn.summer.homework.PO.StudentCourse;
import cn.summer.homework.feignClient.CourseClient;
import cn.summer.homework.service.CourseStudentService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/8/6-23:58
 */

@Service
public class CourseStudentServiceImpl implements CourseStudentService {
    @Resource
    private CourseClient client;

    @Override
    public CourseOpBO dropStudent(StudentCourse sc) {
        return null;
    }

    @Override
    public CourseOpBO addStudent(StudentCourse sc) {
        return null;
    }
}
