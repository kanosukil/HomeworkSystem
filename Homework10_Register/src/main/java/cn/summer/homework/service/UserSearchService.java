package cn.summer.homework.service;

import cn.summer.homework.DTO.UserRoleDTO;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/15-18:30
 */
public interface UserSearchService {
    List<UserRoleDTO> getAll();

    UserRoleDTO get(Integer id);
}
