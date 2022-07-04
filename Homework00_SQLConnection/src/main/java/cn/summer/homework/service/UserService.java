package cn.summer.homework.service;

import cn.summer.homework.DTO.UserInfo;
import cn.summer.homework.Entity.User;

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

    UserInfo getAllUser();

    UserInfo findUser(String username);

    UserInfo findUser(Integer id);
    // username="Op:selectByID"

    UserInfo findUserByEmail(String email);
    // username="Op:selectByEmail"

    UserInfo createNewUser(User user, String role);
    // username="Op:insert"

    /**
     * 删除不能使用 username
     * username 设置为可重复的
     */
    UserInfo deleteUser(Integer id);
    // username="Op:delete"

    UserInfo deleteUser(String email);
    // username="Op:delete"

    UserInfo updateUserInfo(User user);
    // username="Op:update"

    UserInfo updateUserInfo(User user, String role);
    // username="Op:update"

    UserInfo updateUserRole(Integer id, String role);
    // username="Op:update"

    UserInfo deleteUserRole(Integer id, String role);
    // username="Op:update"
}
