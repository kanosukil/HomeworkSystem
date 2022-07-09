package cn.summer.homework.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/3-16:23
 */

@Mapper
public interface QuestionTypeDao {
    List<String> selectAll();

    String selectByID(Integer id);

    Integer selectByName(String typeName);

    int addQuestionType(String typeName);

    int deleteQuestionType(String typeName);

    int deleteByID(Integer id);

    int getLast();
}
