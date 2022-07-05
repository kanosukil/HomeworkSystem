package cn.summer.homework.service.impl;

import cn.summer.homework.DTO.CourseDTO;
import cn.summer.homework.DTO.CourseInfo;
import cn.summer.homework.Entity.Course;
import cn.summer.homework.Entity.User;
import cn.summer.homework.PO.StudentCourse;
import cn.summer.homework.PO.TeacherCourse;
import cn.summer.homework.dao.CourseDao;
import cn.summer.homework.dao.cascade.StudentCourseDao;
import cn.summer.homework.dao.cascade.TeacherCourseDao;
import cn.summer.homework.service.CourseService;
import cn.summer.homework.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author VHBin
 * @date 2022/7/4-15:08
 */

@Service
public class CourseServiceImpl implements CourseService {
    private static final Logger logger = LoggerFactory.getLogger(CourseServiceImpl.class);
    @Resource
    private CourseDao courseDao;
    @Resource
    private UserService userService;
    @Resource
    private StudentCourseDao studentCourseDao;
    @Resource
    private TeacherCourseDao teacherCourseDao;

    private List<User> getStudent(List<StudentCourse> sc) {
        List<User> res = new ArrayList<>();
        sc.forEach(e -> userService.selectByID(e.getSid()));
        return res;
    }

    private List<User> getTeacher(List<TeacherCourse> tc) {
        List<User> res = new ArrayList<>();
        tc.forEach(e -> userService.selectByID(e.getTid()));
        return res;
    }

    private List<Course> getSCourse(List<StudentCourse> sc) {
        List<Course> res = new ArrayList<>();
        sc.forEach(e -> courseDao.selectByID(e.getCid()));
        return res;
    }

    private List<Course> getTCourse(List<TeacherCourse> tc) {
        List<Course> res = new ArrayList<>();
        tc.forEach(e -> courseDao.selectByID(e.getCid()));
        return res;
    }

    private CourseInfo getCourseInfo(String courseName, Integer number, String key, String value) {
        return new CourseInfo(courseName, number, new HashMap<>() {{
            put(key, value);
        }});
    }

    private CourseInfo getCourseInfo(String courseName, Integer number, Map<String, Object> map) {
        return new CourseInfo(courseName, number, map);
    }

    private CourseInfo setCourse(List<Course> courses, String courseName) {
        List<User> teachers;
        List<User> students;
        Integer id = 0;
        Map<String, Object> map = new HashMap<>();
        CourseInfo courseInfo = new CourseInfo();

        for (Course course : courses) {
            id = course.getId();
            teachers = getTeacher(teacherCourseDao.selectByCID(id));
            if (teachers.size() <= 0) {
                courseInfo = getCourseInfo(courseName, -1,
                        "查询 TeacherCourse",
                        "教师未找到, courseID:".concat(id.toString()));
                logger.error("Cause: CourseID: {} 查询 TeacherCourse 表未找到授课教师", id);
                id = -1;
                break;
            }
            students = getStudent(studentCourseDao.selectByCID(id));
            map.put("CourseID:".concat(id.toString()),
                    new CourseDTO(course, teachers, students));
        }
        if (id >= 0) {
            courseInfo =
                    getCourseInfo(courseName, courses.size(), map);
            logger.info("{} 已查询完毕", courseName);
        }
        return courseInfo;
    }

    @Override
    public CourseInfo getAllCourse() {
        return setCourse(courseDao.selectAll(), "Course:All Course");
    }

    @Override
    public CourseInfo getCoursesByTeacher(Integer tid) {
        return setCourse(getTCourse(teacherCourseDao.selectByTID(tid)),
                "Course:".concat(userService.selectByID(tid).getName().concat("-T")));
    }

    @Override
    public CourseInfo getCoursesByStudent(Integer sid) {
        return setCourse(getSCourse(studentCourseDao.selectBySID(sid)),
                "Course:".concat(userService.selectByID(sid).getName().concat("-S")));
    }

    @Override
    public CourseInfo getCourse(Integer id) {
        Course course = courseDao.selectByID(id);
        return setCourse(new ArrayList<>() {{
            add(course);
        }}, "Course:".concat(course.getName()));
    }

    @Override
    public CourseInfo getCourse(String name) {
        return setCourse(courseDao.selectByName(name), "Course:".concat(name));
    }

    @Override
    public CourseInfo createNewCourse(Course course, Integer tid) {
        return null;
    }

    @Override
    public CourseInfo joinCourse(Integer cid, Integer tid) {
        return null;
    }

    @Override
    public CourseInfo chooseCourse(Integer cid, Integer sid) {
        return null;
    }

    @Override
    public CourseInfo updateCourseName(Integer cid, String name) {
        return null;
    }

    @Override
    public CourseInfo dropCourse(Integer cid, Integer sid) {
        return null;
    }

    @Override
    public CourseInfo outCourse(Integer cid, Integer tid) {
        return null;
    }

    @Override
    public CourseInfo deleteCourse(Integer id) {
        return null;
    }

    @Override
    public CourseInfo deleteTeachCourse(Integer tid) {
        return null;
    }

    @Override
    public CourseInfo deleteLearnCourse(Integer sid) {
        return null;
    }
}
