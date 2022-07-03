package cn.summer.homework.dao.cascade;

import cn.summer.homework.PO.QuestionType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/3-16:21
 */

@Mapper
public interface QuestionTypeDao {
    List<QuestionType> selectByQID(Integer qid);

    List<QuestionType> selectByQTID(Integer qtid);

    int createTypeOfQuestion(QuestionType questionType);

    int deleteByQID(Integer qid);

    int deleteByQTID(Integer qtid);

    int accurateDelete(QuestionType questionType);
}
