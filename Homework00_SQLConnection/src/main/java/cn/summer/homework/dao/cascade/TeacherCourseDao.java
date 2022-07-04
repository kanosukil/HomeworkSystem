package cn.summer.homework.dao.cascade;

import cn.summer.homework.PO.TeacherCourse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/3-16:25
 */

@Mapper
public interface TeacherCourseDao {
    List<TeacherCourse> selectByTID(Integer tid);

    List<TeacherCourse> selectByCID(Integer cid);

    int accurateSelect(TeacherCourse teacherCourse);

    int addNewCourse(TeacherCourse teacherCourse);

    int deleteByTID(Integer tid);

    int deleteByCID(Integer cid);

    int accurateDelete(TeacherCourse teacherCourse);
}
