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
    List<ResultCourse> selectByRID(Integer rid);

    List<ResultCourse> selectByCID(Integer cid);

    int addResultOfCourse(ResultCourse resultCourse);

    int deleteByRID(Integer rid);

    int deleteByCID(Integer cid);

    int accurateDelete(ResultCourse resultCourse);
}
