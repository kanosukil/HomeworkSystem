package cn.summer.homework.dao.cascade;

import cn.summer.homework.PO.StudentResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/3-16:24
 */

@Mapper
public interface StudentResultDao {
    List<StudentResult> selectBySID(Integer sid);

    List<StudentResult> selectByRID(Integer rid);

    int accurateSelect(StudentResult studentResult);

    int addNewResult(StudentResult studentResult);

    int deleteByRID(Integer rid);

    int deleteBySID(Integer sid);

    int accurateDelete(StudentResult studentResult);
}
