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
 * @date 2022/7/14-23:12
 */

@FeignClient("SQLService")
public interface UserSQLClient {
    @PostMapping("/user-sql/user-register")
    UserOpBO register(@RequestBody UserRoleDTO newUser);

    @GetMapping("/user-sql/user-login")
    UserRoleDTO login(@RequestParam("email") String email);

    @GetMapping("/user-sql/users-get")
    List<UserRoleDTO> getAll();

    @GetMapping("/user-sql/user-get-id")
    UserRoleDTO get(@RequestParam("id") Integer id);

    @PostMapping("/user-sql/user-delete-id")
    UserOpBO delete(@RequestBody Integer id);

    @PostMapping("/user-sql/user-delete")
    UserOpBO logoff(@RequestBody String email);

    @PostMapping("/user-sql/user-update")
    UserOpBO update(@RequestBody UserRoleDTO updateUser);

    @PostMapping("/user-sql/user-info-update")
    UserOpBO infoUpdate(@RequestBody User user);

    @PostMapping("/user-sql/user-role-update")
    UserOpBO roleUpdate(@RequestBody URoleDTO roles);
}
