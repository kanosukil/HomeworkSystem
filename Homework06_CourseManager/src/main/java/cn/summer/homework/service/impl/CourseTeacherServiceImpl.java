package cn.summer.homework.service.impl;

import cn.summer.homework.BO.CourseOpBO;
import cn.summer.homework.DTO.CourseSTDTO;
import cn.summer.homework.DTO.NewCourseDTO;
import cn.summer.homework.Entity.Course;
import cn.summer.homework.PO.TeacherCourse;
import cn.summer.homework.Util.OpBOUtil;
import cn.summer.homework.feignClient.CourseClient;
import cn.summer.homework.service.CourseTeacherService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

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
        Course course = newCourse.getCourse();
        if (course == null || newCourse.getTid() == 0) {
            return OpBOUtil.generateCOB("新建课程传入的数据不能为空");
        }
        if (course.getName() == null) {
            return OpBOUtil.generateCOB("新建课程名不能为空");
        }
        course.setCreate_time(new Date());
        course.setTeacher_num(1);
        course.setStudent_num(0);
        newCourse.setCourse(course);
        return client.createCourse(newCourse);
    }

    @Override
    public CourseOpBO addTeacher(TeacherCourse tc) {
        if (tc == null || tc.getTid() == null || tc.getCid() == null) {
            return OpBOUtil.generateCOB("添加老师到课程传入数据不能为空");
        }
        return client.addTeacher(tc);
    }

    @Override
    public CourseOpBO dropTeacher(TeacherCourse tc) {
        if (tc == null || tc.getTid() == null || tc.getCid() == null) {
            return OpBOUtil.generateCOB("删除师从课程传入数据不能为空");
        }
        return client.dropTeacher(tc);
    }

    @Override
    public CourseOpBO update(NewCourseDTO updateCourse) {
        Course course = updateCourse.getCourse();
        if (course == null || updateCourse.getTid() == 0) {
            return OpBOUtil.generateCOB("更新课程的传入数据不能为空");
        }
        if (course.getName() == null || course.getName().equals("")
                || course.getId() == 0) {
            return OpBOUtil.generateCOB("更新课程的数据不能为空");
        }
        try {
            CourseSTDTO before = client.get(course.getId());
            course.setTeacher_num(before.getTeachers().size());
            course.setStudent_num(before.getStudents().size());
            course.setCreate_time(before.getCourse().getCreate_time());
            updateCourse.setCourse(course);
            return client.update(updateCourse);
        } catch (Exception ex) {
            return OpBOUtil
                    .generateCOB("更新课程获取数据异常[推测:courseID错误]");
        }
    }

    @Override
    public CourseOpBO delete(TeacherCourse tc) {
        if (tc == null || tc.getCid() == null || tc.getTid() == 0) {
            return OpBOUtil.generateCOB("删除课程传入的数据不能为空");
        }
        return client.delete(tc);
    }
}
