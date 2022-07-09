package cn.summer.homework.dao.cascade;

import cn.summer.homework.PO.QuestionType;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/3-16:21
 */

@Mapper
public interface Question_TypeDao {
    List<Integer> selectByQID(Integer qid);

    List<Integer> selectByQTID(Integer qtid);

    int accurateSelect(QuestionType questionType);

    int createTypeOfQuestion(QuestionType questionType);

    int deleteByQID(Integer qid);

    int deleteByQTID(Integer qtid);

    int accurateDelete(QuestionType questionType);
}
