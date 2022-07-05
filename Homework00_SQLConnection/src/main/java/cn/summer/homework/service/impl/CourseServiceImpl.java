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
import java.util.*;

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
        return setCourse(new ArrayList<>(Collections.singletonList(course)),
                "Course:".concat(course.getName()));
    }

    @Override
    public CourseInfo getCourse(String name) {
        return setCourse(courseDao.selectByName(name), "Course:".concat(name));
    }

    @Override
    public CourseInfo createNewCourse(Course course, Integer tid) {
        CourseInfo courseInfo;
        if (userService.isTeacher(tid)) {
            if (courseDao.createNewCourse(course) > 0) {
                // 对于吞吐量小的课程创建可行
                // 但是在同一时刻创建了相同名字课程时, 该方法将产生不可重复读问题
                List<Course> courses = courseDao.selectByName(course.getName());
                Course c = courses.get(courses.size() - 1);
                Integer id = c.getId();
                if (teacherCourseDao.addNewCourse(new TeacherCourse(tid, id)) > 0) {
                    courseInfo = getCourseInfo("Op:insert", 1,
                            new HashMap<>() {{
                                put("Course:ID".concat(id.toString()),
                                        new CourseDTO(c,
                                                new ArrayList<>(Collections.singletonList(
                                                        userService.selectByID(tid))),
                                                new ArrayList<>()));
                            }});
                    logger.info("TeacherID: {} CourseID: {} 插入成功", tid, id);
                } else {
                    logger.error("TeacherID: {} CourseID: {} 插入 TeacherCourse 失败", tid, id);
                    int reset = courseDao.deleteCourse(id);
                    if (reset > 0) {
                        logger.info("CourseID: {} 删除成功", id);
                    } else {
                        logger.error("CourseID: {} 删除失败", id);
                    }
                    courseInfo = getCourseInfo("Op:insert", -1,
                            "插入 TeacherCourse", "插入失败".concat(reset > 0 ? "" : ", 但 Course 已创建"));
                } // teacherCourse
            } else {
                courseInfo = getCourseInfo("Op:insert", -1,
                        "创建 Course", "创建失败");
                logger.error("TeacherID: {} 创建 Course 失败", tid);
            } // course
        } else {
            courseInfo = getCourseInfo("Op:insert", 0,
                    "查询 User", "未找到用户/用户权限不够");
            logger.error("UserID: {} 不存在/权限不够", tid);
        }
        return courseInfo;
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
