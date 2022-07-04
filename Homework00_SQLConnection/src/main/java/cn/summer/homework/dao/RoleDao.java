package cn.summer.homework.dao;

import org.apache.ibatis.annotations.Mapper;

/**
 * @author VHBin
 * @date 2022/7/3-16:16
 */

@Mapper
public interface RoleDao {
    String selectByID(Integer id);

    Integer selectByName(String roleName);
}
