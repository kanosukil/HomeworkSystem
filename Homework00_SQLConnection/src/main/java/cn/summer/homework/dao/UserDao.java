package cn.summer.homework.dao;

import cn.summer.homework.Entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/3-12:24
 */

@Mapper
public interface UserDao {

    List<User> selectAll();

    List<User> selectByName(String name);

    User selectByEmail(String email);

    int insertNewUser(User user);

    int deleteUser(Integer id);

    int updateUser(User user);
}
