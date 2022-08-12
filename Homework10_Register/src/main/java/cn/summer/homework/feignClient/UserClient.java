package cn.summer.homework.feignClient;

import cn.summer.homework.BO.UserOpBO;
import cn.summer.homework.DTO.URoleDTO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.Entity.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/15-15:10
 */

@FeignClient("UserSQL")
public interface UserClient {
    @PostMapping("/user/user-register")
    UserOpBO register(@RequestBody UserRoleDTO newUser);

    @GetMapping("/user/user-login")
    UserRoleDTO login(@RequestParam("email") String email);

    @GetMapping("/user/users-get")
    List<UserRoleDTO> getAll();

    @GetMapping("/user/user-get-id")
    UserRoleDTO get(@RequestParam("id") Integer id);

    @PostMapping("/user/user-delete-id")
    UserOpBO delete(@RequestBody Integer id);

    @PostMapping("/user/user-delete")
    UserOpBO logoff(@RequestBody String email);

    @PostMapping("/user/user-update")
    UserOpBO update(@RequestBody UserRoleDTO updateUser);

    @PostMapping("/user/user-info-update")
    UserOpBO infoUpdate(@RequestBody User user);

    @PostMapping("/user/user-role-update")
    UserOpBO roleUpdate(@RequestBody URoleDTO roles);
}
