package cn.summer.homework.dao.cascade;

import cn.summer.homework.PO.StudentCourse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/3-16:24
 */

@Mapper
public interface StudentCourseDao {
    List<Integer> selectBySID(Integer sid);

    List<Integer> selectByCID(Integer cid);

    int accurateSelect(StudentCourse studentCourse);

    int addNewStudentOfCourse(StudentCourse studentCourse);

    int deleteBySID(Integer sid);

    int deleteByCID(Integer cid);

    int accurateDelete(StudentCourse studentCourse);
}
