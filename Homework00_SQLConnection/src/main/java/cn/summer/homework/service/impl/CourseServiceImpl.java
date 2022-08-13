package cn.summer.homework.service.impl;

import cn.summer.homework.BO.CourseOpBO;
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
import cn.summer.homework.service.HomeworkService;
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
    @Resource
    private HomeworkService homeworkService;

    private List<User> getStudent(List<Integer> sc) {
        List<User> res = new ArrayList<>();
        sc.forEach(e -> res.add(userService.findUser(e).getUser()));
        return res;
    }

    private List<User> getTeacher(List<Integer> tc) {
        List<User> res = new ArrayList<>();
        tc.forEach(e -> res.add(userService.findUser(e).getUser()));
        return res;
    }

    private List<Course> getSCourse(List<Integer> sc) {
        List<Course> res = new ArrayList<>();
        sc.forEach(e -> res.add(courseDao.selectByID(e)));
        return res;
    }

    private List<Course> getTCourse(List<Integer> tc) {
        List<Course> res = new ArrayList<>();
        tc.forEach(e -> res.add(courseDao.selectByID(e)));
        return res;
    }

    private void setCourseOpDTO(CourseOpBO courseOpBO, String key, String value) {
        courseOpBO.setIsSuccess(false);
        courseOpBO.setMap(new HashMap<>() {{
            put(key, value);
        }});
    }

    private void setCourseOpDTO(CourseOpBO courseOpBO, Map<String, Object> map) {
        courseOpBO.setIsSuccess(true);
        courseOpBO.setMap(map);
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
    public CourseOpBO createNewCourse(Course course, Integer tid)
            throws SQLRWException {
        CourseOpBO courseOpBO = new CourseOpBO();
        int flag = 0;
        int[] insert = new int[2];

        try {
            if (!userService.isTeacher(tid)) {
                throw new Exception("用户不存在/用户权限不够. UserID=".concat(tid.toString()));
            }
            flag = 1;
            insert[0] = courseDao.createNewCourse(course);
            flag = 2;
            Course finalCourse = courseDao.selectByID(courseDao.getLast());
            Integer cid = finalCourse.getId();
            insert[1] = teacherCourseDao.addNewCourse(new TeacherCourse(tid, cid));
            flag = 3;
            logger.info("UserID: {} 创建课程 CourseID: {} 成功", tid, cid);
            logger.info("Course 表插入 {} 条数据", insert[0]);
            logger.info("TeacherCourse 表插入 {} 条数据", insert[1]);
            setCourseOpDTO(courseOpBO, new HashMap<>() {{
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
            if (flag == 1 || flag == 2) {
                throw new SQLRWException("Course/TeacherCourse 插入异常");
            }
            setCourseOpDTO(courseOpBO,
                    flag == 3 ? "创建 Course 完成, 但仍有异常" : "创建 Course 失败",
                    ex.getMessage());

        }
        return courseOpBO;
    }

    private void updateExLog(CourseOpBO courseOpBO, int flag, Exception ex)
            throws SQLRWException {
        switch (flag) {
            case 0 -> logger.error("查询异常: {}", ex.getMessage());
            case 1 -> logger.error("Course 更新异常: {}", ex.getMessage());
            case 2 -> logger.error("TeacherCourse 插入/删除异常: {}", ex.getMessage());
            default -> logger.error("其他异常: {}", ex.getMessage());
        }
        if (flag == 1 || flag == 2) {
            throw new SQLRWException("Course/TeacherCourse/StudentCourse 插入/删除异常");
        }
        setCourseOpDTO(courseOpBO,
                "Cause",
                ex.getMessage());

    }

    private void judgeTeacher(Integer tid, Integer cid, Integer afterFindCID)
            throws Exception {
        if (!userService.isTeacher(tid)) {
            throw new Exception("用户不存在/用户权限不够");
        }
        if (!Objects.equals(afterFindCID, cid)) {
            throw new Exception("课程不存在");
        }
        if (teacherCourseDao.accurateSelect(new TeacherCourse(tid, cid)) <= 0) {
            throw new Exception("用户权限不够");
        }
    }

    private void judgeStudent(Integer sid, Integer cid, Integer afterFindCID)
            throws Exception {
        if (!userService.isStudent(sid)) {
            throw new Exception("用户不存在/用户权限不够");
        }
        if (!Objects.equals(afterFindCID, cid)) {
            throw new Exception("课程不存在");
        }
        if (studentCourseDao.accurateSelect(new StudentCourse(sid, cid)) <= 0) {
            throw new Exception("用户权限不够");

        }
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public CourseOpBO joinCourse(Integer cid, Integer tid)
            throws SQLRWException {
        CourseOpBO courseOpBO = new CourseOpBO();
        int flag = 0;
        int[] update = new int[2];

        try {
            Course course = courseDao.selectByID(cid);
            if (course == null) {
                throw new Exception("课程不存在");
            }
            judgeTeacher(tid, cid, course.getId());
            flag = 1;
            CourseSTDTO srcCourseSTDTO = getCourseSTDTO(course);
            course.setTeacher_num(course.getTeacher_num() + 1);
            update[0] = courseDao.updateCourse(course);
            flag = 2;
            update[1] = teacherCourseDao.addNewCourse(new TeacherCourse(tid, cid));
            flag = 3;
            logger.info("老师 UserID: {} 加入课程完成", tid);
            logger.info("Course 表更新 {} 条数据", update[0]);
            logger.info("TeacherCourse 表插入 {} 条数据", update[1]);
            setCourseOpDTO(courseOpBO, new HashMap<>() {{
                put("srcCourse", srcCourseSTDTO);
                put("updateCourse", getCourseSTDTO(cid));
            }});
        } catch (Exception ex) {
            logger.error("UserID: {} 加入 TeacherCourse 异常", tid);
            updateExLog(courseOpBO, flag, ex);
        }
        return courseOpBO;
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public CourseOpBO chooseCourse(Integer cid, Integer sid)
            throws SQLRWException {
        CourseOpBO courseOpBO = new CourseOpBO();
        int flag = 0;
        int[] update = new int[2];

        try {
            Course course = courseDao.selectByID(cid);
            if (course == null) {
                throw new Exception("课程不存在");
            }
            judgeStudent(sid, cid, course.getId());
            flag = 1;
            CourseSTDTO srcCourseSTDTO = getCourseSTDTO(course);
            course.setStudent_num(course.getStudent_num() + 1);
            update[0] = courseDao.updateCourse(course);
            flag = 2;
            update[1] = studentCourseDao
                    .addNewStudentOfCourse(new StudentCourse(sid, cid));
            flag = 3;
            logger.info("学生 UserID: {} 选修课程完成", sid);
            logger.info("Course 表更新 {} 条数据", update[0]);
            logger.info("StudentCourse 表插入 {} 条数据", update[1]);
            setCourseOpDTO(courseOpBO, new HashMap<>() {{
                put("srcCourse", srcCourseSTDTO);
                put("updateCourse", getCourseSTDTO(cid));
            }});
        } catch (Exception ex) {
            logger.error("UserID: {} 加入 StudentCourse 异常", sid);
            updateExLog(courseOpBO, flag, ex);
        }
        return courseOpBO;
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public CourseOpBO updateCourseName(Integer uid, Integer cid, String name)
            throws SQLRWException {
        CourseOpBO courseOpBO = new CourseOpBO();
        int flag = 0;

        try {
            Course course = courseDao.selectByID(cid);
            if (course == null) {
                throw new Exception("课程不存在");
            }
            judgeTeacher(uid, cid, course.getId());
            flag = 1;
            course.setName(name);
            CourseSTDTO srcCourseSTDTO = getCourseSTDTO(course);
            int update = courseDao.updateCourse(course);
            flag = 3;
            logger.info("CourseID: {} 课程名更新完成", cid);
            logger.info("Course 表更新 {} 条数据", update);
            setCourseOpDTO(courseOpBO, new HashMap<>() {{
                put("srcCourse", srcCourseSTDTO);
                put("updateCourse", getCourseSTDTO(cid));
            }});
        } catch (Exception ex) {
            logger.error("CourseID: {} 更新异常", cid);
            updateExLog(courseOpBO, flag, ex);
        }

        return courseOpBO;
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public CourseOpBO dropCourse(Integer cid, Integer sid)
            throws SQLRWException {
        CourseOpBO courseOpBO = new CourseOpBO();
        int flag = 0;
        int[] update = new int[2];

        try {
            Course course = courseDao.selectByID(cid);
            if (course == null) {
                throw new Exception("课程不存在");
            }
            judgeStudent(sid, cid, course.getId());
            flag = 1;
            CourseSTDTO srcCourseSTDTO = getCourseSTDTO(course);
            course.setStudent_num(course.getStudent_num() - 1);
            update[0] = courseDao.updateCourse(course);
            flag = 2;
            update[1] = studentCourseDao
                    .accurateDelete(new StudentCourse(sid, cid));
            flag = 3;
            homeworkService.selectHKBySID(sid).forEach(e ->
            {
                try {
                    int num = homeworkService.deleteResult(sid, e.getResult().getId()).getInfo().size();
                    logger.info("删除了 {} 个学生回答", num);
                } catch (SQLRWException ex) {
                    throw new RuntimeException("学生退课时删除 homework 失败", ex);
                }
            });
            logger.info("学生 UserID: {} 退课完成", sid);
            logger.info("Course 表更新 {} 条数据", update[0]);
            logger.info("StudentCourse 表删除 {} 条数据", update[1]);
            setCourseOpDTO(courseOpBO, new HashMap<>() {{
                put("srcCourse", srcCourseSTDTO);
                put("updateCourse", getCourseSTDTO(cid));
            }});
        } catch (Exception ex) {
            logger.error("UserID: {} 从 StudentCourse 删去异常", sid);
            updateExLog(courseOpBO, flag, ex);
        }
        return courseOpBO;
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public CourseOpBO outCourse(Integer cid, Integer tid)
            throws SQLRWException {
        CourseOpBO courseOpBO = new CourseOpBO();
        int flag = 0;
        int[] update = new int[2];

        try {
            Course course = courseDao.selectByID(cid);
            if (course == null) {
                throw new Exception("课程不存在");
            }
            judgeTeacher(tid, cid, course.getId());
            CourseSTDTO srcCourseSTDTO = getCourseSTDTO(course);
            CourseSTDTO afterCourseSTDTO;
            if (course.getTeacher_num() == 1) {
                // 该老师为最后一个任课老师时, 直接删除课程
                CourseOpBO courseDelete = deleteCourse(tid, cid);
                if (!courseDelete.getIsSuccess()) {
                    throw new Exception("课程删除异常");
                }
                afterCourseSTDTO =
                        new CourseSTDTO(null, null, null);
            } else {
                flag = 1;
                course.setTeacher_num(course.getTeacher_num() - 1);
                update[0] = courseDao.updateCourse(course);
                flag = 2;
                update[1] = teacherCourseDao.addNewCourse(new TeacherCourse(tid, cid));
                flag = 3;
                afterCourseSTDTO = getCourseSTDTO(cid);
            }
            logger.info("老师 UserID: {} 退出课程完成", tid);
            logger.info("Course 表更新 {} 条数据", update[0]);
            logger.info("TeacherCourse 表删除 {} 条数据", update[1]);
            setCourseOpDTO(courseOpBO, new HashMap<>() {{
                put("srcCourse", srcCourseSTDTO);
                put("updateCourse", afterCourseSTDTO);
            }});
        } catch (Exception ex) {
            logger.error("UserID: {} 从 TeacherCourse 删去异常", tid);
            updateExLog(courseOpBO, flag, ex);
        }
        return courseOpBO;
    }

    private void deleteExLog(CourseOpBO courseOpBO, int flag, Exception ex)
            throws SQLRWException {
        switch (flag) {
            case 0 -> logger.error("查询异常: {}", ex.getMessage());
            case 1 -> logger.error("TeacherCourse 删除异常: {}", ex.getMessage());
            case 2 -> logger.error("StudentCourse 删除异常: {}", ex.getMessage());
            case 3 -> logger.error("Course 删除异常: {}", ex.getMessage());
            default -> logger.error("其他异常: {}", ex.getMessage());
        }
        if (flag <= 3 && flag >= 1) {
            throw new SQLRWException("Course/TeacherCourse/StudentCourse 删除异常");
        }
        setCourseOpDTO(courseOpBO,
                "Cause",
                ex.getMessage());
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public CourseOpBO deleteCourse(Integer uid, Integer cid)
            throws SQLRWException {
        CourseOpBO courseOpBO = new CourseOpBO();
        int flag = 0;
        int[] delete = new int[3];

        try {
            Course course = courseDao.selectByID(cid);
            if (course == null) {
                throw new Exception("课程不存在");
            }
            judgeTeacher(uid, cid, course.getId());
            CourseSTDTO srcCourseSTDTO = getCourseSTDTO(course);
            homeworkService.selectHKByCID_T(cid).forEach(e
                    -> {
                try {
                    int size = homeworkService.deleteQuestion(uid, e.getQuestion().getId()).getInfo().size();
                    logger.info("删除了 {} 个问题", size);
                } catch (SQLRWException ex) {
                    throw new RuntimeException("老师删除课程时删除 homework 失败", ex);
                }
            });
            flag = 1;
            delete[0] = teacherCourseDao.deleteByCID(cid);
            flag = 2;
            delete[1] = studentCourseDao.deleteByCID(cid);
            flag = 3;
            delete[2] = courseDao.deleteCourse(cid);
            flag = 5;
            logger.info("CourseID: {} 删除完成", cid);
            logger.info("Course 删除了 {} 条数据", delete[2]);
            logger.info("TeacherCourse 删除了 {} 条数据", delete[0]);
            logger.info("StudentCourse 删除了 {} 条数据", delete[1]);
            setCourseOpDTO(courseOpBO, new HashMap<>() {{
                put("被删除课程", srcCourseSTDTO);
            }});
        } catch (Exception ex) {
            logger.error("CourseID: {} 删除异常", cid);
            deleteExLog(courseOpBO, flag, ex);
        }
        return courseOpBO;
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public CourseOpBO deleteTeachCourse(Integer tid)
            throws SQLRWException {
        CourseOpBO courseOpBO = new CourseOpBO();
        int flag = 0;
        int[] delete = new int[4];

        try {
            if (!userService.isTeacher(tid)) {
                throw new Exception("用户不存在/用户非老师");
            }
            List<Course> tCourse = getTCourse(teacherCourseDao.selectByTID(tid));
            homeworkService.selectHKByTID(tid).forEach(e -> {
                try {
                    int size = homeworkService.deleteQuestion(tid, e.getQuestion().getId()).getInfo().size();
                    logger.info("删除了 {} 个问题", size);
                } catch (SQLRWException ex) {
                    throw new RuntimeException("老师删除课程时删除 homework 失败", ex);
                }
            });
            flag = 1;
            delete[0] = teacherCourseDao.deleteByTID(tid);
            flag = 2;
            delete[1] = 0;
            tCourse.forEach(e ->
                    delete[1] += studentCourseDao.deleteByCID(e.getId()));
            flag = 3;
            delete[2] = 0;
            tCourse.forEach(e ->
                    delete[2] += courseDao.deleteCourse(e.getId()));
            flag = 4;
//            UserOpBO deleteUser = userService.deleteUser(tid);
//            if (!deleteUser.getIsSuccess()) {
//                throw new Exception("课程删除完毕, 用户删除异常");
//            }
//            delete[3] = deleteUser.getInfo().size();
            flag = 5;
            logger.info("Teacher UserID: {} 删除完成", tid);
//            logger.info("User 删除了 {} 条数据", delete[3]);
            logger.info("Course 删除了 {} 条数据", delete[2]);
            logger.info("TeacherCourse 删除了 {} 条数据", delete[0]);
            logger.info("StudentCourse 删除了 {} 条数据", delete[1]);
            setCourseOpDTO(courseOpBO, new HashMap<>() {{
                tCourse.forEach(e -> put(tid.toString() + "\t"
                                + e.getId() + "\t" + e.getName(),
                        getCourseSTDTO(e)));
            }});
        } catch (Exception ex) {
            logger.error("Teacher UserID: {} 销号异常 ", tid);
            deleteExLog(courseOpBO, flag, ex);
        }
        return courseOpBO;
    }

    @Override
    @Transactional(rollbackFor = SQLRWException.class)
    public CourseOpBO deleteLearnCourse(Integer sid)
            throws SQLRWException {
        CourseOpBO courseOpBO = new CourseOpBO();
        int flag = 0;
        int[] delete = new int[2];

        try {
            if (!userService.isStudent(sid)) {
                throw new Exception("用户不存在/用户不是学生");
            }
            List<Course> sCourse = getSCourse(studentCourseDao.selectBySID(sid));
            homeworkService.selectHKBySID(sid).forEach(e -> {
                try {
                    int size = homeworkService.deleteResult(sid, e.getResult().getId()).getInfo().size();
                    logger.info("删除了 {} 条回答", size);
                } catch (SQLRWException ex) {
                    throw new RuntimeException("学生销号时删除 homework 失败", ex);
                }
            });
            flag = 2;
            delete[0] = studentCourseDao.deleteBySID(sid);
            flag = 4;
//            UserOpBO deleteUser = userService.deleteUser(sid);
//            if (!deleteUser.getIsSuccess()) {
//                throw new Exception("课程删除完毕, 用户删除异常");
//            }
//            delete[1] = deleteUser.getInfo().size();
            flag = 5;
            logger.info("Student UserID: {} 删除完成", sid);
//            logger.info("User 删除了 {} 条数据", delete[1]);
            logger.info("StudentCourse 删除了 {} 条数据", delete[0]);
            setCourseOpDTO(courseOpBO, new HashMap<>() {{
                sCourse.forEach(e -> put(sid.toString() + "\t"
                                + e.getId() + "\t" + e.getName(),
                        getCourseSTDTO(e)));
            }});
        } catch (Exception ex) {
            logger.error("Student UserID: {} 销号异常", sid);
            deleteExLog(courseOpBO, flag, ex);
        }

        return courseOpBO;
    }

    @Override
    public boolean isTeachingByTeacher(Integer cid, Integer tid) {
        List<User> teachers = getCourse(cid).getTeachers();
        if (teachers.size() > 0) {
            for (User teacher : teachers) {
                if (teacher.getId().equals(tid)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isLearningByStudent(Integer cid, Integer sid) {
        List<User> students = getCourse(cid).getStudents();
        if (students.size() > 0) {
            for (User student : students) {
                if (student.getId().equals(sid)) {
                    return true;
                }
            }
        }
        return false;
    }
}
