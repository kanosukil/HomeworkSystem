package cn.summer.homework.controller;

import cn.summer.homework.BO.UserOpBO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.Entity.User;
import cn.summer.homework.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/10-13:39
 */

@RestController
@RefreshScope
@RequestMapping("user")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Resource
    private UserService userService;

    private void setRes(UserOpBO userOpBO, boolean isSuccess, String key, Object value) {
        userOpBO.setIsSuccess(isSuccess);
        userOpBO.setInfo(new HashMap<>(1, 1f) {{
            put(key, value);
        }});
    }

    @PostMapping("user-register")
    public UserOpBO register(@RequestBody UserRoleDTO newUser) {
        UserOpBO res = new UserOpBO();
        List<String> roles = newUser.getRoles();
        try {
            UserOpBO user =
                    userService.createNewUser(newUser.getUser(), roles.get(0));
            if (!user.getIsSuccess()) {
                throw new Exception("创建用户失败");
            }
            Integer uid =
                    ((UserRoleDTO) user.getInfo().get("新增用户"))
                            .getUser().getId();
            for (int i = 1; i < roles.size(); i++) {
                user = userService.updateUserRole(uid, roles.get(i));
                if (!user.getIsSuccess()) {
                    throw new Exception("添加用户角色失败");
                }
            }
            logger.info("注册完成");
            setRes(res, true, "uid", uid);
        } catch (Exception ex) {
            logger.error("用户创建失败: {}", ex.getMessage());
            setRes(res, false, "Cause", ex.getCause());
        }
        return res;
    }

    @GetMapping("user-login")
    public UserRoleDTO login(@RequestBody String email) {
        return userService.findUserByEmail(email);
    }

    @GetMapping("users-get")
    public List<UserRoleDTO> getAll() {
        return userService.getAllUser();
    }

    @PostMapping("user-delete")
    public UserOpBO logoff(@RequestBody Integer id) {
        UserOpBO res = new UserOpBO();
        try {
            if (userService.isExists(id)) {
                UserOpBO user = userService.deleteUser(id);
                if (user.getIsSuccess()) {
                    setRes(res, true, "原信息", user.getInfo());
                    logger.info("销号完成");
                } else {
                    throw new Exception("销号失败");
                }
            }
        } catch (Exception ex) {
            logger.error("销号失败: {}", ex.getMessage());
            setRes(res, false, "Cause", ex.getCause());
        }
        return res;
    }

    @PostMapping("user-update")
    public UserOpBO update(@RequestBody UserRoleDTO updateUser) {
        UserOpBO res = new UserOpBO();
        try {
            List<String> roles = updateUser.getRoles();
            User u = updateUser.getUser();
            Integer uid = u.getId();
            UserRoleDTO srcUser = userService.findUser(uid);
            UserOpBO user =
                    userService.updateUserInfo(u, roles.get(0));
            if (!user.getIsSuccess()) {
                throw new Exception("更新用户失败");
            }
            for (int i = 1; i < roles.size(); i++) {
                user = userService.updateUserRole(uid, roles.get(i));
                if (!user.getIsSuccess()) {
                    throw new Exception("更新用户角色失败");
                }
            }
            logger.info("更新");
            setRes(res, true, "srcUser", srcUser);
        } catch (Exception ex) {
            logger.error("用户更新失败: {}", ex.getMessage());
            setRes(res, false, "Cause", ex.getCause());
        }
        return res;
    }
}
