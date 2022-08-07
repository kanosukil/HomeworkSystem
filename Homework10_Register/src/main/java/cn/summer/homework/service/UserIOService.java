package cn.summer.homework.service;

import cn.summer.homework.BO.UserOpBO;
import cn.summer.homework.DTO.URoleDTO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.Entity.User;

import java.io.IOException;

/**
 * @author VHBin
 * @date 2022/7/15-18:31
 */
public interface UserIOService {
    UserOpBO register(UserRoleDTO newUser);

    UserRoleDTO login(String email) throws IOException;

    UserOpBO delete(Integer id);

    UserOpBO logoff(String email);

    UserOpBO update(UserRoleDTO updateUser);

    UserOpBO infoUpdate(User user);

    UserOpBO roleUpdate(URoleDTO roles);
}
