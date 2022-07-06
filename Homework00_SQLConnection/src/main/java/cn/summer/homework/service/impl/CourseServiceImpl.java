package cn.summer.homework.service.impl;

import cn.summer.homework.DTO.CourseOpDTO;
import cn.summer.homework.DTO.CourseSTDTO;
import cn.summer.homework.Entity.Course;
import cn.summer.homework.Entity.User;
import cn.summer.homework.PO.StudentCourse;
import cn.summer.homework.PO.TeacherCourse;
import cn.summer.homework.dao.CourseDao;
import cn.summer.homework.dao.cascade.StudentCourseDao;
import cn.summer.homework.dao.cascade.TeacherCourseDao;
import cn.summer.homework.exception.SQLRWException;
import cn.summer.homework.service.CourseService;
import cn.summer.homework.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        sc.forEach(e -> res.add(userService.findUser(e.getSid())));
        return res;
    }

    private List<User> getTeacher(List<TeacherCourse> tc) {
        List<User> res = new ArrayList<>();
        tc.forEach(e -> res.add(userService.findUser(e.getTid())));
        return res;
    }

    private List<Course> getSCourse(List<StudentCourse> sc) {
        List<Course> res = new ArrayList<>();
        sc.forEach(e -> res.add(courseDao.selectByID(e.getCid())));
        return res;
    }

    private List<Course> getTCourse(List<TeacherCourse> tc) {
        List<Course> res = new ArrayList<>();
        tc.forEach(e -> res.add(courseDao.selectByID(e.getCid())));
        return res;
    }

    private void setCourseOpDTO(CourseOpDTO courseOpDTO, String key, String value) {
        courseOpDTO.setIsSuccess(false);
        courseOpDTO.setMap(new HashMap<>() {{
            put(key, value);
        }});
    }

    private void setCourseOpDTO(CourseOpDTO courseOpDTO, Map<String, Object> map) {
        courseOpDTO.setIsSuccess(true);
        courseOpDTO.setMap(map);
    }

    private CourseSTDTO getCourseSTDTO(Integer cid) {
        return new CourseSTDTO(courseDao.selectByID(cid),
                getTeacher(teacherCourseDao.selectByCID(cid)),
                getStudent(studentCourseDao.selectByCID(cid)));
    }

    private CourseSTDTO getCourseSTDTO(Course course) {
        Integer id = course.getId();
        return new CourseSTDTO(course,
                getTeacher(teacherCourseDao.selectByCID(id)),
                getStudent(studentCourseDao.selectByCID(id)));
    }

    @Override
    public List<CourseSTDTO> getAllCourse() {
        List<CourseSTDTO> courseSTDTOs = new ArrayList<>();
        courseDao.selectAll().forEach(e
                -> courseSTDTOs.add(getCourseSTDTO(e)));
        logger.info("All Course 获取完成");
        return courseSTDTOs;
    }

    @Override
    public List<CourseSTDTO> getCoursesByTeacher(Integer tid) {
        List<CourseSTDTO> courseSTDTOs = new ArrayList<>();
        if (userService.isTeacher(tid)) {
            List<Course> tCourse = getTCourse(teacherCourseDao.selectByTID(tid));
            tCourse.forEach(e -> courseSTDTOs.add(getCourseSTDTO(e)));
            logger.info("Course 获取完成 By TeacherID: {}", tid);
        } else {
            logger.error("UserID: {} 用户不存在/用户权限不够(非T)", tid);
            courseSTDTOs.add(new CourseSTDTO(null, new ArrayList<>(), null));
        }
        return courseSTDTOs;
    }

    @Override
    public List<CourseSTDTO> getCoursesByStudent(Integer sid) {
        List<CourseSTDTO> courseSTDTOs = new ArrayList<>();
        if (userService.isStudent(sid)) {
            List<Course> sCourse = getSCourse(studentCourseDao.selectBySID(sid));
            sCourse.forEach(e -> courseSTDTOs.add(getCourseSTDTO(e)));
            logger.info("Course 获取完成 By StudentID: {}", sid);
        } else {
            logger.error("UserID: {} 用户不存在/用户权限不够(非S)", sid);
            courseSTDTOs.add(new CourseSTDTO(null, null, new ArrayList<>()));
        }
        return courseSTDTOs;
    }

    @Override
    public CourseSTDTO getCourse(Integer id) {
        CourseSTDTO courseSTDTO;
        if (id <= 0) {
            courseSTDTO = new CourseSTDTO(new Course(), null, null);
            logger.error("CourseID: {} 不符合要求", id);
        } else {
            courseSTDTO = getCourseSTDTO(id);
            logger.info("CourseID: {} 查询完毕", id);
        }
        return courseSTDTO;
    }

    @Override
    public List<CourseSTDTO> getCourse(String name) {
        List<CourseSTDTO> courseSTDTOs = new ArrayList<>();
        courseDao.selectByName(name).forEach(e ->
                courseSTDTOs.add(getCourseSTDTO(e)));
        logger.info("Course 获取完成 By CourseName: {}", name);
        return courseSTDTOs;
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public CourseOpDTO createNewCourse(Course course, Integer tid)
            throws SQLRWException {
        CourseOpDTO courseOpDTO = new CourseOpDTO();
        int flag = 0;
        int[] insert = new int[2];

        try {
            if (!userService.isTeacher(tid)) {
                throw new Exception("用户不存在/用户权限不够. UserID=".concat(tid.toString()));
            }
            flag = 1;
            insert[0] = courseDao.createNewCourse(course);
            flag = 2;
            List<Course> courses = courseDao.selectByName(course.getName());
            Course finalCourse = courses.get(courses.size() - 1);
            Integer cid = finalCourse.getId();
            insert[1] = teacherCourseDao.addNewCourse(new TeacherCourse(tid, cid));
            flag = 3;
            logger.info("UserID: {} 创建课程 CourseID: {} 成功", tid, cid);
            logger.info("Course 表插入 {} 条数据", insert[0]);
            logger.info("TeacherCourse 表插入 {} 条数据", insert[1]);
            setCourseOpDTO(courseOpDTO, new HashMap<>() {{
                put("新建课程", getCourseSTDTO(finalCourse));
            }});
        } catch (Exception ex) {
            logger.error("UserID: {} 插入课程失败", tid);
            switch (flag) {
                case 0 -> logger.error("查询异常: {}", ex.getMessage());
                case 1 -> logger.error("Course 插入异常: {}", ex.getMessage());
                case 2 -> logger.error("TeacherCourse 插入异常: {} ", ex.getMessage());
                default -> logger.error("未知异常: {}", ex.getMessage());
            }
            setCourseOpDTO(courseOpDTO,
                    flag == 3 ? "创建 Course 完成, 但仍有异常" : "创建 Course 失败",
                    ex.getMessage());
            if (flag == 1 || flag == 2) {
                throw new SQLRWException("Course/TeacherCourse 插入异常");
            }
        }
        return courseOpDTO;
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public CourseOpDTO joinCourse(Integer cid, Integer tid) {
        CourseOpDTO courseOpDTO = new CourseOpDTO();
        int flag = 0;
        int[] update = new int[2];

        try {
            if (!userService.isTeacher(tid)) {
                throw new Exception("用户不存在/用户权限不够");
            }
            Course course = courseDao.selectByID(cid);
            if (!Objects.equals(course.getId(), cid)) {
                throw new Exception("课程不存在");
            }
            flag = 1;
            course.setTeacher_num(course.getTeacher_num() + 1);
            update[0] = courseDao.updateCourse(course);
            flag = 2;
            

        } catch (Exception ex) {

        }
        return courseOpDTO;
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public CourseOpDTO chooseCourse(Integer cid, Integer sid) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public CourseOpDTO updateCourseName(Integer cid, String name) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public CourseOpDTO dropCourse(Integer cid, Integer sid) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public CourseOpDTO outCourse(Integer cid, Integer tid) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public CourseOpDTO deleteCourse(Integer id) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public CourseOpDTO deleteTeachCourse(Integer tid) {
        return null;
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public CourseOpDTO deleteLearnCourse(Integer sid) {
        return null;
    }
}
