package cn.summer.homework.dao.cascade;

import cn.summer.homework.PO.TeacherQuestion;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/3-16:25
 */

@Mapper
public interface TeacherQuestionDao {
    List<TeacherQuestion> selectByTID(Integer tid);

    List<TeacherQuestion> selectByQID(Integer qid);

    int accurateSelect(TeacherQuestion teacherQuestion);

    int addNewQuestion(TeacherQuestion teacherQuestion);

    int deleteByTID(Integer tid);

    int deleteByQID(Integer qid);

    int accurateDelete(TeacherQuestion teacherQuestion);
}
