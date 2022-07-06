package cn.summer.homework.service;

import cn.summer.homework.DTO.UserOpDTO;
import cn.summer.homework.Entity.User;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/4-15:02
 */

public interface UserService {
    boolean isStudent(Integer id);

    boolean isTeacher(Integer id);

    boolean isAdmin(Integer id);

    boolean isExists(String name);

    boolean isExists(Integer id);

    boolean isEmailUsed(String email);

    List<User> getAllUser();

    List<User> findUser(String username);

    User findUser(Integer id);

    User findUserByEmail(String email);

    UserOpDTO createNewUser(User user, String role);

    /**
     * 删除不能使用 username
     * username 设置为可重复的
     */
    UserOpDTO deleteUser(Integer id);

    UserOpDTO deleteUser(String email);

    UserOpDTO updateUserInfo(User user);

    UserOpDTO updateUserInfo(User user, String role);

    UserOpDTO updateUserRole(Integer id, String role);

    UserOpDTO deleteUserRole(Integer id, String role);
}
