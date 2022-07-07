package cn.summer.homework.dao.cascade;

import cn.summer.homework.PO.ResultCourse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/3-16:24
 */

@Mapper
public interface ResultCourseDao {
    List<Integer> selectByRID(Integer rid);

    List<Integer> selectByCID(Integer cid);

    int accurateSelect(ResultCourse resultCourse);

    int addResultOfCourse(ResultCourse resultCourse);

    int deleteByRID(Integer rid);

    int deleteByCID(Integer cid);

    int accurateDelete(ResultCourse resultCourse);
}
