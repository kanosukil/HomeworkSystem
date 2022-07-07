package cn.summer.homework.dao;

import cn.summer.homework.Entity.Question;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/3-16:19
 */

@Mapper
public interface QuestionDao {
    List<Question> selectAll();

    Question selectByID(Integer id);

    int createNewQuestion(Question question);

    int deleteByID(Integer id);

    int updateQuestion(Question question);

    int getLast();
}
