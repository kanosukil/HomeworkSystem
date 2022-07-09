package cn.summer.homework;

import cn.summer.homework.exception.SQLRWException;
import cn.summer.homework.service.CourseService;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/7/7-18:07
 */

@SpringBootTest
public class MyBatisTests {
    private static final Logger logger = LoggerFactory.getLogger(MyBatisTests.class);
    @Resource
    private CourseService course;

    @Test
    void startTest() throws SQLRWException {
        logger.info("Course");
        logger.info("Get All: {}", course.getAllCourse());
        logger.info("GetByTeacher: {}", course.getCoursesByTeacher(1));
        logger.info("GetByStudent: {}", course.getCoursesByStudent(1));
        logger.info("GetByCID: {}", course.getCourse(1));
        logger.info("GetByCourseName: {}", course.getCourse("1"));

    }
}
