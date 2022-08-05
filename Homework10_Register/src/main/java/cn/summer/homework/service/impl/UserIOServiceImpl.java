package cn.summer.homework.service.impl;

import cn.summer.homework.BO.UserOpBO;
import cn.summer.homework.DTO.URoleDTO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.Entity.User;
import cn.summer.homework.feignClient.UserClient;
import cn.summer.homework.service.UserIOService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/8/5-13:26
 */

@Service
public class UserIOServiceImpl implements UserIOService {

    @Resource
    private UserClient client;

    @Override
    public UserOpBO register(UserRoleDTO newUser) {
        return client.register(newUser);
    }

    @Override
    public UserRoleDTO login(String email) {
        return client.login(email);
    }

    @Override
    public UserOpBO delete(Integer id) {
        return client.delete(id);
    }

    @Override
    public UserOpBO logoff(String email) {
        return client.logoff(email);
    }

    @Override
    public UserOpBO update(UserRoleDTO updateUser) {
        return client.update(updateUser);
    }

    @Override
    public UserOpBO infoUpdate(User user) {
        return client.infoUpdate(user);
    }

    @Override
    public UserOpBO roleUpdate(URoleDTO roles) {
        return client.roleUpdate(roles);
    }
}
