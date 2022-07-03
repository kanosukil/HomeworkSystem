package cn.summer.homework.dao;

import cn.summer.homework.Entity.Course;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/3-16:19
 */

@Mapper
public interface CourseDao {
    List<Course> selectAll();

    Course selectByID(Integer id);

    int createNewCourse(Course course);

    int deleteCourse(Integer id);

    int updateCourse(Course course);
}
