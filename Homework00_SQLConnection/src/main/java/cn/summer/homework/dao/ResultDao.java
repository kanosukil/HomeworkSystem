package cn.summer.homework.dao;

import cn.summer.homework.Entity.Result;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/3-16:19
 */

@Mapper
public interface ResultDao {
    List<Result> selectAll();

    Result selectByID(Integer id);

    int createNewResult(Result result);

    int deleteByID(Integer id);

    int updateResult(Result result);

    int getLast();
}
