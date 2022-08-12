package cn.summer.homework.controller;

import cn.summer.homework.BO.CourseOpBO;
import cn.summer.homework.BO.UserOpBO;
import cn.summer.homework.DTO.URoleDTO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.Entity.User;
import cn.summer.homework.service.CourseService;
import cn.summer.homework.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author VHBin
 * @date 2022/7/10-13:39
 */

@RestController
@RefreshScope
@RequestMapping("user-sql")
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    @Resource
    private UserService userService;
    @Resource
    private CourseService courseService;

    private void setRes(UserOpBO userOpBO, boolean isSuccess, String key, Object value) {
        userOpBO.setIsSuccess(isSuccess);
        userOpBO.setInfo(new HashMap<>(1, 1f) {{
            put(key, value);
        }});
    }

    /*
        注册
     */
    @PostMapping("user-register")
    public UserOpBO register(@RequestBody UserRoleDTO newUser) {
        UserOpBO res = new UserOpBO();
        List<String> roles = newUser.getRoles();
        try {
            User createUser = newUser.getUser();
            if (userService.isEmailUsed(createUser.getEmail())) {
                throw new Exception("邮箱已被使用");
            }
            UserOpBO user =
                    userService.createNewUser(createUser, roles.get(0));
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
            setRes(res, false, "Cause", ex.getMessage());
        }
        return res;
    }

    /*
        登录获取目标用户信息
     */
    @GetMapping("user-login")
    public UserRoleDTO login(@RequestParam("email") String email) {
        return userService.findUserByEmail(email);
    }
    // ../user-login?email=xxx

    /*
        获取所有用户
     */
    @GetMapping("users-get")
    public List<UserRoleDTO> getAll() {
        return userService.getAllUser();
    }

    @GetMapping("user-get-id")
    public UserRoleDTO get(@RequestParam("id") Integer id) {
        return userService.findUser(id);
    }

    /*
        注销
     */
    @PostMapping("user-delete-id")
    public UserOpBO delete(@RequestBody Integer id) {
        UserOpBO res = new UserOpBO();
        try {
            if (userService.isExists(id)) {
                CourseOpBO course = new CourseOpBO();
                if (userService.isStudent(id)) {
                    course = courseService.deleteLearnCourse(id);
                }
                if (userService.isTeacher(id)) {
                    course = courseService.deleteTeachCourse(id);
                }
                if (!course.getIsSuccess()) {
                    throw new Exception("删除选课/任课失败");
                }
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
            setRes(res, false, "Cause", ex.getMessage());
        }
        return res;
    }

    @PostMapping("user-delete")
    public UserOpBO logoff(@RequestBody String email) {
        UserOpBO res = new UserOpBO();
        try {
            if (!userService.isEmailUsed(email)) {
                throw new Exception("用户不存在");
            }
            UserRoleDTO userByEmail = userService.findUserByEmail(email);
            Integer uid = userByEmail.getUser().getId();
            AtomicBoolean isTeacher = new AtomicBoolean(false);
            AtomicBoolean isStudent = new AtomicBoolean(false);
            userByEmail.getRoles().forEach(e -> {
                if (e.equals("Teacher")) isTeacher.set(true);
                if (e.equals("Student")) isStudent.set(true);
            });
            CourseOpBO course = new CourseOpBO();
            if (isTeacher.get()) {
                course = courseService.deleteTeachCourse(uid);
            }
            if (isStudent.get()) {
                course = courseService.deleteLearnCourse(uid);
            }
            if (!course.getIsSuccess()) {
                throw new Exception("删除选课/任课失败");
            }
            UserOpBO user = userService.deleteUser(email);
            if (user.getIsSuccess()) {
                setRes(res, true, "原信息", userByEmail.getUser());
                logger.info("销号完成");
            } else {
                throw new Exception("销号失败");
            }

        } catch (Exception ex) {
            logger.error("销号失败: {}", ex.getMessage());
            setRes(res, false, "Cause", ex.getMessage());
        }
        return res;
    }

    /*
        数据更新
     */
    @PostMapping("user-update")
    public UserOpBO update(@RequestBody UserRoleDTO updateUser) {
        UserOpBO res = new UserOpBO();
        try {
            User u = updateUser.getUser();
            Integer uid = u.getId();
            UserRoleDTO srcUser = userService.findUser(uid);
            UserOpBO info = infoUpdate(u);
            if (!info.getIsSuccess()) {
                throw new Exception("用户数据更新失败");
            }
            UserOpBO role = roleUpdate(new URoleDTO(uid, updateUser.getRoles()));
            if (!role.getIsSuccess()) {
                throw new Exception("用户角色更新失败");
            }
            logger.info("更新完成");
            setRes(res, true, "srcUser", srcUser);
        } catch (Exception ex) {
            logger.error("用户更新失败: {}", ex.getMessage());
            setRes(res, false, "Cause", ex.getMessage());
        }
        return res;
    }

    @PostMapping("user-info-update")
    public UserOpBO infoUpdate(@RequestBody User user) {
        UserOpBO res = new UserOpBO();
        try {
            UserRoleDTO srcUser = userService.findUser(user.getId());
            UserOpBO update = userService.updateUserInfo(user);
            if (!update.getIsSuccess()) {
                throw new Exception("更新用户数据失败");
            }
            logger.info("更新完成");
            setRes(res, true, "srcUser", srcUser);
        } catch (Exception ex) {
            logger.error("用户数据更新失败: {}", ex.getMessage());
            setRes(res, false, "Cause", ex.getMessage());
        }
        return res;
    }

    @PostMapping("user-role-update")
    public UserOpBO roleUpdate(@RequestBody URoleDTO roles) {
        UserOpBO res = new UserOpBO();
        Integer uid = roles.getUid();
        try {
            UserRoleDTO srcUser = userService.findUser(uid);
            Map<String, Boolean> map = new HashMap<>();
            srcUser.getRoles().forEach(e -> map.put(e, true));
            for (String role : roles.getRoles()) {
                UserOpBO update;
                if (map.get(role)) {
                    update = userService.deleteUserRole(uid, role);
                } else {
                    update = userService.updateUserRole(uid, role);
                }
                if (!update.getIsSuccess()) {
                    throw new Exception("更新用户角色失败");
                }
            }
            logger.info("更新完成");
            setRes(res, true, "srcUser", srcUser);
        } catch (Exception ex) {
            logger.error("用户角色更新失败: {}", ex.getMessage());
            setRes(res, false, "Cause", ex.getMessage());
        }
        return res;
    }
}
