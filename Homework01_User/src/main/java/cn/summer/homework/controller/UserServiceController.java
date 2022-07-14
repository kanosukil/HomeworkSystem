package cn.summer.homework.controller;

import cn.summer.homework.BO.UserOpBO;
import cn.summer.homework.DTO.URoleDTO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.Entity.User;
import cn.summer.homework.feignClient.UserSQLClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/14-23:53
 */

@RefreshScope
@RestController
@RequestMapping("user")
public class UserServiceController {
    @Resource
    private UserSQLClient sqlClient;

    @PostMapping("user-register")
    public UserOpBO register(@RequestBody UserRoleDTO newUser) {
        return sqlClient.register(newUser);
    }

    @GetMapping("user-login")
    public UserRoleDTO login(@RequestParam("email") String email) {
        return sqlClient.login(email);
    }

    @GetMapping("users-get")
    public List<UserRoleDTO> getAll() {
        return sqlClient.getAll();
    }

    @GetMapping("user-get-id")
    public UserRoleDTO get(@RequestParam("id") Integer id) {
        return sqlClient.get(id);
    }

    @PostMapping("user-delete-id")
    public UserOpBO delete(@RequestBody Integer id) {
        return sqlClient.delete(id);
    }

    @PostMapping("user-delete")
    public UserOpBO logoff(@RequestBody String email) {
        return sqlClient.logoff(email);
    }

    @PostMapping("user-update")
    public UserOpBO update(@RequestBody UserRoleDTO updateUser) {
        return sqlClient.update(updateUser);
    }

    @PostMapping("user-info-update")
    public UserOpBO infoUpdate(@RequestBody User user) {
        return sqlClient.infoUpdate(user);
    }

    @PostMapping("user-role-update")
    public UserOpBO roleUpdate(@RequestBody URoleDTO roles) {
        return sqlClient.roleUpdate(roles);
    }

}
