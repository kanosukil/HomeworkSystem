package cn.summer.homework.controller;

import cn.summer.homework.BO.CourseOpBO;
import cn.summer.homework.DTO.CourseSTDTO;
import cn.summer.homework.DTO.NewCourseDTO;
import cn.summer.homework.Entity.Course;
import cn.summer.homework.PO.StudentCourse;
import cn.summer.homework.PO.TeacherCourse;
import cn.summer.homework.service.CourseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/12-13:53
 */

@RefreshScope
@RestController
@RequestMapping("course-sql")
public class CourseController {
    private static final Logger logger = LoggerFactory.getLogger(CourseController.class);
    @Resource
    private CourseService courseService;

    /*
        查询
     */
    @GetMapping("courses-get")
    public List<CourseSTDTO> getAll() {
        return courseService.getAllCourse();
    }

    @GetMapping("course-get")
    public CourseSTDTO get(@RequestParam("id") Integer id) {
        return courseService.getCourse(id);
    }

    @GetMapping("courses-get-name")
    public List<CourseSTDTO> getByName(@RequestParam("name") String name) {
        return courseService.getCourse(name);
    }

    @GetMapping("courses-get-teacher")
    public List<CourseSTDTO> getByTeacher(@RequestParam("tid") Integer tid) {
        return courseService.getCoursesByTeacher(tid);
    }

    @GetMapping("courses-get-student")
    public List<CourseSTDTO> getByStudent(@RequestParam("sid") Integer sid) {
        return courseService.getCoursesByStudent(sid);
    }

    private void setRes(CourseOpBO res, Boolean isSuccess, String key, Object value) {
        res.setIsSuccess(isSuccess);
        res.setMap(new HashMap<>(1, 1f) {{
            put(key, value);
        }});
    }

    /*
        创建
     */
    @PostMapping("create-course")
    public CourseOpBO createCourse(@RequestBody NewCourseDTO newCourse) {
        CourseOpBO res = new CourseOpBO();
        try {
            CourseOpBO createCourse = courseService.createNewCourse(newCourse.getCourse(),
                    newCourse.getTid());
            if (!createCourse.getIsSuccess()) {
                throw new Exception("创建课程失败");
            }
            logger.info("创建课程完成");
            setRes(res, true, "cid",
                    ((CourseSTDTO) createCourse.getMap().get("新建课程"))
                            .getCourse()
                            .getId());
        } catch (Exception ex) {
            logger.error("创建课程异常: {}", ex.getMessage());
            setRes(res, false, "Cause", ex.getCause());
        }
        return res;
    }

    /*
        学生/教师的加入/退出
     */
    @PostMapping("course-add-student")
    public CourseOpBO addStudent(@RequestBody StudentCourse sc) {
        CourseOpBO res = new CourseOpBO();
        try {
            CourseOpBO studentAdd = courseService.chooseCourse(sc.getCid(), sc.getSid());
            if (!studentAdd.getIsSuccess()) {
                throw new Exception("添加学生失败");
            }
            logger.info("学生选课完成");
            setRes(res, true, "Course",
                    studentAdd.getMap().get("updateCourse"));
        } catch (Exception ex) {
            logger.error("课程添加学生异常: {}", ex.getMessage());
            setRes(res, false, "Cause", ex.getCause());
        }
        return res;
    }

    @PostMapping("course-add-teacher")
    public CourseOpBO addTeacher(@RequestBody TeacherCourse tc) {
        CourseOpBO res = new CourseOpBO();
        try {
            CourseOpBO teacherAdd = courseService.joinCourse(tc.getCid(), tc.getTid());
            if (!teacherAdd.getIsSuccess()) {
                throw new Exception("添加教师失败");
            }
            logger.info("教师加入完成");
            setRes(res, true, "Course",
                    teacherAdd.getMap().get("updateCourse"));
        } catch (Exception ex) {
            logger.error("课程添加教师异常: {}", ex.getMessage());
            setRes(res, false, "Cause", ex.getCause());
        }
        return res;
    }

    @PostMapping("course-drop-student")
    public CourseOpBO dropStudent(@RequestBody StudentCourse sc) {
        CourseOpBO res = new CourseOpBO();
        try {
            CourseOpBO studentDrop = courseService.dropCourse(sc.getCid(), sc.getSid());
            if (!studentDrop.getIsSuccess()) {
                throw new Exception("删除学生失败");
            }
            logger.info("学生退课完成");
            setRes(res, true, "Course",
                    studentDrop.getMap().get("updateCourse"));
        } catch (Exception ex) {
            logger.error("课程移除学生异常: {}", ex.getMessage());
            setRes(res, false, "Cause", ex.getCause());
        }
        return res;
    }

    @PostMapping("course-drop-teacher")
    public CourseOpBO dropTeacher(@RequestBody TeacherCourse tc) {
        CourseOpBO res = new CourseOpBO();
        try {
            CourseOpBO teacherDrop = courseService.outCourse(tc.getCid(), tc.getTid());
            if (!teacherDrop.getIsSuccess()) {
                throw new Exception("删除教师失败");
            }
            logger.info("教师退课完成");
            setRes(res, true, "Course",
                    teacherDrop.getMap().get("updateCourse"));
        } catch (Exception ex) {
            logger.error("课程移除教师异常: {}", ex.getMessage());
            setRes(res, false, "Cause", ex.getCause());
        }
        return res;
    }

    /*
        课程更新
     */
    @PostMapping("course-update-name")
    public CourseOpBO update(@RequestBody NewCourseDTO updateCourse) {
        CourseOpBO res = new CourseOpBO();
        try {
            Course course = updateCourse.getCourse();
            CourseOpBO updateName
                    = courseService.updateCourseName(
                    updateCourse.getTid(),
                    course.getId(),
                    course.getName()
            );
            if (!updateName.getIsSuccess()) {
                throw new Exception("更新课程名失败");
            }
            logger.info("课程名更新完成");
            setRes(res, true, "Course",
                    updateName.getMap().get("updateCourse"));
        } catch (Exception ex) {
            logger.error("课程更新课程名异常: {}", ex.getMessage());
            setRes(res, false, "Cause", ex.getCause());
        }
        return res;
    }

    /*
        删除
     */
    @PostMapping("course-delete")
    public CourseOpBO delete(@RequestBody TeacherCourse tc) {
        CourseOpBO res = new CourseOpBO();

        try {
            CourseOpBO courseDelete =
                    courseService.deleteCourse(tc.getTid(), tc.getCid());
            if (!courseDelete.getIsSuccess()) {
                throw new Exception("删除课程失败");
            }
            logger.info("课程删除完成");
            setRes(res, true, "Course",
                    courseDelete.getMap().get("被删除课程"));
        } catch (Exception ex) {
            logger.error("课程删除异常: {}", ex.getMessage());
            setRes(res, false, "Cause", ex.getCause());
        }

        return res;
    }
}
