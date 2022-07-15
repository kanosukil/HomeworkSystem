package cn.summer.homework.service;

import cn.summer.homework.BO.CourseOpBO;
import cn.summer.homework.PO.StudentCourse;

/**
 * @author VHBin
 * @date 2022/7/15-18:29
 */
public interface CourseStudentService {
    CourseOpBO dropStudent(StudentCourse sc);

    CourseOpBO addStudent(StudentCourse sc);
}
