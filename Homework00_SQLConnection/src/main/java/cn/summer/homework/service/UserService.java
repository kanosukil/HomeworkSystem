package cn.summer.homework.service;

import cn.summer.homework.BO.UserOpBO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.Entity.User;
import cn.summer.homework.exception.SQLRWException;

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

    boolean isExists(Integer id) throws NullPointerException;

    boolean isEmailUsed(String email) throws NullPointerException;

    List<UserRoleDTO> getAllUser();

    List<UserRoleDTO> findUser(String username);

    UserRoleDTO findUser(Integer id);

    UserRoleDTO findUserByEmail(String email) throws NullPointerException;

    UserOpBO createNewUser(User user, String role) throws SQLRWException;

    /**
     * 删除不能使用 username
     * username 设置为可重复的
     */
    UserOpBO deleteUser(Integer id) throws SQLRWException;

    UserOpBO deleteUser(String email) throws SQLRWException;

    UserOpBO updateUserInfo(User user) throws SQLRWException;

    UserOpBO updateUserInfo(User user, String role) throws SQLRWException;

    UserOpBO updateUserRole(Integer id, String role) throws SQLRWException;

    UserOpBO deleteUserRole(Integer id, String role) throws SQLRWException;
}
