package cn.summer.homework.dao.cascade;

import cn.summer.homework.PO.QuestionResult;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/3-16:20
 */

@Mapper
public interface QuestionResultDao {
    List<QuestionResult> selectByQID(Integer qid);

    List<QuestionResult> selectByRID(Integer rid);

    int createResultOfQuestion(QuestionResult questionResult);

    int deleteByRID(Integer rid);

    int deleteByQID(Integer qid);

    int accurateDelete(QuestionResult questionResult);
}
