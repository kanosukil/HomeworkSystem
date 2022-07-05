package cn.summer.homework.service;

import cn.summer.homework.DTO.CourseInfo;
import cn.summer.homework.Entity.Course;

/**
 * @author VHBin
 * @date 2022/7/4-15:07
 */
public interface CourseService {
    CourseInfo getAllCourse();
    // 获取所有课程
    // courseName="Course:All Course"

    CourseInfo getCoursesByTeacher(Integer tid);
    // 获得某个老师教授的课程
    // courseName="Course:TeacherName-T"

    CourseInfo getCoursesByStudent(Integer sid);
    // 获得某个学生选修的课程
    // courseName="Course:StudentName-S"

    CourseInfo getCourse(Integer id);
    // 获得指定课程信息 ByID
    // courseName="Course:CourseName"

    CourseInfo getCourse(String name);
    // 获得指定课程信息 ByName
    // courseName="Course:CourseName"

    CourseInfo createNewCourse(Course course, Integer tid);
    // 老师新建课程
    // courseName="Op:insert"

    CourseInfo joinCourse(Integer cid, Integer tid);
    // 新老师加入一个已建课程共同教授
    // Course teacher_num 需要修改, Teacher_Course 需要插入
    // courseName="Op:insert"

    CourseInfo chooseCourse(Integer cid, Integer sid);
    // 学生选修课程
    // Course student_num 需要修改, Student_Course 需要插入
    // courseName="Op:update"

    CourseInfo updateCourseName(Integer cid, String name);
    // 老师修改课程名
    // courseName="Op:update"

    CourseInfo dropCourse(Integer cid, Integer sid);
    // 学生退课
    // Course student_num 需要修改, Student_Course 需要删除
    // courseName="Op:update"

    CourseInfo outCourse(Integer cid, Integer tid);
    // 老师退出授课名单
    // Course teacher_num 需要修改, Teacher_Course 需要删除
    // courseName="Op:insert"

    CourseInfo deleteCourse(Integer id);
    // 管理员\教师删除指定课程
    // courseName="Op:delete"

    CourseInfo deleteTeachCourse(Integer tid);
    // 老师销号
    // courseName="Op:delete"

    CourseInfo deleteLearnCourse(Integer sid);
    // 学生销号
    // courseName="Op:delete"
}
