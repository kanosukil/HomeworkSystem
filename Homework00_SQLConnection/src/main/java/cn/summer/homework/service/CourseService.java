package cn.summer.homework.service;

import cn.summer.homework.DTO.CourseOpDTO;
import cn.summer.homework.DTO.CourseSTDTO;
import cn.summer.homework.Entity.Course;
import cn.summer.homework.exception.SQLRWException;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/4-15:07
 */
public interface CourseService {
    List<CourseSTDTO> getAllCourse();
    // 获取所有课程

    List<CourseSTDTO> getCoursesByTeacher(Integer tid);
    // 获得某个老师教授的课程

    List<CourseSTDTO> getCoursesByStudent(Integer sid);
    // 获得某个学生选修的课程

    CourseSTDTO getCourse(Integer id);
    // 获得指定课程信息 ByID

    List<CourseSTDTO> getCourse(String name);
    // 获得指定课程信息 ByName

    CourseOpDTO createNewCourse(Course course, Integer tid) throws SQLRWException;
    // 老师新建课程

    CourseOpDTO joinCourse(Integer cid, Integer tid) throws SQLRWException;
    // 新老师加入一个已建课程共同教授
    // Course teacher_num 需要修改, Teacher_Course 需要插入

    CourseOpDTO chooseCourse(Integer cid, Integer sid) throws SQLRWException;
    // 学生选修课程
    // Course student_num 需要修改, Student_Course 需要插入

    CourseOpDTO updateCourseName(Integer cid, String name) throws SQLRWException;
    // 老师修改课程名

    CourseOpDTO dropCourse(Integer cid, Integer sid) throws SQLRWException;
    // 学生退课
    // Course student_num 需要修改, Student_Course 需要删除

    CourseOpDTO outCourse(Integer cid, Integer tid) throws SQLRWException;
    // 老师退出授课名单
    // Course teacher_num 需要修改, Teacher_Course 需要删除

    CourseOpDTO deleteCourse(Integer id) throws SQLRWException;
    // 管理员\教师删除指定课程

    CourseOpDTO deleteTeachCourse(Integer tid) throws SQLRWException;
    // 老师销号

    CourseOpDTO deleteLearnCourse(Integer sid) throws SQLRWException;
    // 学生销号
}
