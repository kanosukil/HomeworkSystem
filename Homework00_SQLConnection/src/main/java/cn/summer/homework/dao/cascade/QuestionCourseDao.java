package cn.summer.homework.dao.cascade;

import cn.summer.homework.PO.QuestionCourse;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/3-16:20
 */

@Mapper
public interface QuestionCourseDao {
    List<QuestionCourse> selectByQID(Integer qid);

    List<QuestionCourse> selectByCID(Integer cid);

    int accurateSelect(QuestionCourse questionCourse);

    int createQuestionOfCourse(QuestionCourse questionCourse);

    int deleteByQID(Integer qid);

    int deleteByCID(Integer cid);

    int accurateDelete(QuestionCourse questionCourse);
}
