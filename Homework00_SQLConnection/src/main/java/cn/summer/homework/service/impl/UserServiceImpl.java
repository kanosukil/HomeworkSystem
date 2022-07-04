package cn.summer.homework.service.impl;

import cn.summer.homework.DTO.UserDTO;
import cn.summer.homework.DTO.UserInfo;
import cn.summer.homework.Entity.User;
import cn.summer.homework.PO.UserRole;
import cn.summer.homework.dao.RoleDao;
import cn.summer.homework.dao.UserDao;
import cn.summer.homework.dao.cascade.UserRoleDao;
import cn.summer.homework.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author VHBin
 * @date 2022/7/4-15:07
 */

@Service
public class UserServiceImpl implements UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Resource
    private UserDao userDao;
    @Resource
    private RoleDao roleDao;
    @Resource
    private UserRoleDao userRoleDao;

    // user->userRole<-role
    @Override
    public boolean isStudent(Integer id) {
        List<Integer> roles = userRoleDao.selectByUser(id);
        Integer student = roleDao.selectByName("Student");
        for (Integer role : roles) {
            if (role.equals(student))
                return true;
        }
        return false;
    }

    // role->userRole==id
    @Override
    public boolean isTeacher(Integer id) {
        Integer teacher = roleDao.selectByName("Teacher");
        List<Integer> users = userRoleDao.selectByRole(teacher);
        AtomicBoolean res = new AtomicBoolean(false);
        users.forEach(e -> {
            if (e.equals(id)) {
                res.set(true);
            }
        });
        return res.get();
    }

    // user->userRole->role
    @Override
    public boolean isAdmin(Integer id) {
        List<Integer> roles = userRoleDao.selectByUser(id);
        for (Integer role : roles) {
            if ("Admin".equals(roleDao.selectByID(role)))
                return true;
        }
        return false;
    }

    // 用户是否存在
    @Override
    public boolean isExists(String name) {
        return userDao.selectByName(name).size() > 0;
    }

    @Override
    public boolean isExists(Integer id) {
        return userDao.selectByID(id).getId().equals(id);
    }

    @Override
    public boolean isEmailUsed(String email) {
        return userDao.selectByEmail(email).getEmail().equals(email);
    }

    // 用户信息传输体设置
    private void setUserInfo(UserInfo userInfo, String username, int number,
                             String key, String value) {
        userInfo.setUsername(username);
        userInfo.setNumber(number);
        userInfo.setInfo(new HashMap<>() {{
            put(key, value);
        }});
    }

    private void setUserInfo(UserInfo userInfo, String username,
                             int number, Map<String, Object> map) {
        userInfo.setUsername(username);
        userInfo.setNumber(number);
        userInfo.setInfo(map);
    }

    // 查找用户
    @Override
    public UserInfo getAllUser() {
        UserInfo userInfo = new UserInfo();
        List<User> users = userDao.selectAll();
        Map<String, Object> map = new HashMap<>();
        String[] roles;
        for (User user : users) {
            roles = getRoles(user.getId());
            if (roles.length > 0) {
                map.put(user.getEmail(), new UserDTO(user, roles));
            } else {
                map.clear();
                map.put("Error", user);
                break;
            }
        }
        if (!map.containsKey("Error")) {
            setUserInfo(userInfo, "All User", users.size(), map);
            logger.info("获取全部用户信息完成");
        } else {
            Integer id = ((User) map.get("Error")).getId();
            setUserInfo(userInfo, "All User", -1,
                    "获取 User 角色信息失败", id.toString());
            logger.error("获取 User: {} 角色信息失败", id);
        }
        return userInfo;
    }

    @Override
    public UserInfo findUser(String username) {
        UserInfo userInfo = new UserInfo();

        if (isExists(username)) {
            List<User> users = userDao.selectByName(username);
            HashMap<String, Object> map = new HashMap<>();
            List<String> roles = new ArrayList<>();

            for (User user : users) {
                userRoleDao.selectByUser(user.getId()).forEach(e ->
                        roles.add(roleDao.selectByID(e)));
                if (roles.size() == 0) {
                    setUserInfo(userInfo, username, -1,
                            "Error", "角色获取异常");
                    logger.error("Cause: 角色获取异常");
                    break;
                }
                map.put(user.getEmail(), new UserDTO(
                        user, roles.toArray(new String[0])));
                roles.clear();
            }
            if (userInfo.getNumber() != -1) {
                setUserInfo(userInfo, username, users.size(), map);
                logger.info("UserName: {} 已找到", username);
            }
        } else {
            setUserInfo(userInfo, username, 0,
                    "查询 User", "用户不存在");
            logger.error("Cause: 用户不存在");
        }

        return userInfo;
    }

    @Override
    public UserInfo findUser(Integer id) {
        UserInfo userInfo = new UserInfo();

        if (isExists(id)) {
            List<String> roles = new ArrayList<>();
            User user = userDao.selectByID(id);

            userRoleDao.selectByUser(id).forEach(e ->
                    roles.add(roleDao.selectByID(e)));
            if (roles.size() == 0) {
                setUserInfo(userInfo, "Op:SelectByID", -1,
                        "Error", "角色获取异常");
                logger.error("Cause: 角色获取异常");
            } else {
                setUserInfo(userInfo, "Op:SelectByID", 1,
                        new HashMap<>() {{
                            put(user.getEmail(),
                                    new UserDTO(user,
                                            roles.toArray(new String[0])));
                        }});
                logger.info("UserID: {} 已找到", id);
            }
        } else {
            setUserInfo(userInfo, "Op:SelectByID", 0,
                    "查询 User", "用户不存在");
            logger.error("Cause: 用户不存在");
        }
        return userInfo;
    }

    @Override
    public UserInfo findUserByEmail(String email) {
        UserInfo userInfo = new UserInfo();

        if (isEmailUsed(email)) {
            List<String> roles = new ArrayList<>();
            User user = userDao.selectByEmail(email);

            userRoleDao.selectByUser(user.getId()).forEach(e
                    -> roles.add(roleDao.selectByID(e)));
            if (roles.size() == 0) {
                setUserInfo(userInfo, "Op:SelectByEmail", -1,
                        "Error", "角色获取异常");
                logger.error("Cause: 角色获取异常");
            } else {
                setUserInfo(userInfo, "Op:SelectByEmail", 1,
                        new HashMap<>() {{
                            put(email,
                                    new UserDTO(user,
                                            roles.toArray(new String[0])));
                        }});
                logger.info("UserEmail: {} 已找到", email);
            }
        } else {
            setUserInfo(userInfo, "Op:SelectByEmail", 0,
                    " 查询 User", "用户不存在");
            logger.error("Cause: 用户不存在");
        }
        return null;
    }

    // 新建角色
    @Override
    public UserInfo createNewUser(User user, String role) {
        UserInfo userInfo = new UserInfo();

        Integer r = roleDao.selectByName(role);
        if (r < 1 || r > 3) {
            setUserInfo(userInfo, "Op:insert", r,
                    "查询 Role 表", "角色不存在");
            logger.error("Cause: role: {} 不存在", role);
        } else {
            if (userDao.insertNewUser(user) > 0) {
                User finalUser = userDao.selectByEmail(user.getEmail());
                if (userRoleDao.addNewUser(new UserRole(user.getId(), r)) > 0) {
                    setUserInfo(userInfo, "Op:insert", 1,
                            new HashMap<>() {{
                                put(finalUser.getEmail(),
                                        new UserDTO(finalUser, new String[]{role}));
                            }});
                    logger.info("UserID: {} 创建成功", finalUser.getId());
                } else {
                    logger.error("Cause: UserRole 表插入失败");
                    int reset = userDao.deleteUser(finalUser.getId());
                    if (reset > 0) {
                        logger.info("新建 User 已删除");
                    } else {
                        logger.error("新建 User 删除失败!新建 User 已存在于 User 表中.");
                    }
                    setUserInfo(userInfo, "Op:insert", -1,
                            "插入 UserRole 表",
                            "插入失败".concat(reset > 0 ? "" : ", 但 User 已创建"));
                } // userRoleDao
            } else {
                setUserInfo(userInfo, "Op:insert", r,
                        " 插入 User 表", "插入失败");
                logger.error("Cause: UserEmail: {} 插入失败", user.getEmail());
            } // user
        }
        return userInfo;
    }

    private String[] getRoles(Integer id) {
        List<String> roles = new ArrayList<>();
        userRoleDao.selectByUser(id).forEach(e
                -> roles.add(roleDao.selectByID(e)));
        return roles.toArray(roles.toArray(new String[0]));
    }

    // 销号
    @Override
    public UserInfo deleteUser(Integer id) {
        UserInfo userInfo = new UserInfo();

        if (isExists(id)) {
            // 备份原用户的角色集
            String[] role = getRoles(id);
            if (role.length > 0) {
                // 删除用户在 userRole 表中的映射
                if (userRoleDao.deleteUser(id) > 0) {
                    // 备份原用户信息
                    User user = userDao.selectByID(id);
                    // 删除 user 表中的用户信息
                    if (userDao.deleteUser(id) > 0) {
                        setUserInfo(userInfo, "Op:delete", 1,
                                new HashMap<>() {{
                                    put("srcUser", new UserDTO(user, role));
                                }});
                        logger.info("UserID: {} 已成功删除", id);
                    } else {
                        setUserInfo(userInfo, "Op:delete", -1,
                                "删除 User ByID", "User 表删除失败");
                        logger.error("Cause: User 表删除失败! UserID: {} 仍存在于 User 表中", id);
                    } // userDao
                } else {
                    setUserInfo(userInfo, "Op:delete", -1,
                            "删除 UserRole 表 ByID", "UserRole 表删除失败");
                    logger.error("Cause: UserRole 表删除失败! UserID: {} 仍存在于 User 表和 UserRole 表中", id);
                } // userRoleDao
            } else {
                setUserInfo(userInfo, "Op:delete", -1,
                        "Error", "角色获取异常");
                logger.error("Cause: 角色获取异常 DeleteByID");
            } // role = 0
        } else {
            setUserInfo(userInfo, "Op:delete", 0,
                    "删除 User 表操作 ByID", "用户不存在");
            logger.error("Cause: User 表中不存在 UserID: {} 的用户", id);
        }
        return userInfo;
    }

    @Override
    public UserInfo deleteUser(String email) {
        UserInfo userInfo = new UserInfo();

        if (isEmailUsed(email)) {
            User user = userDao.selectByEmail(email);
            String[] role = getRoles(user.getId());
            if (role.length > 0) {
                if (userRoleDao.deleteUser(user.getId()) > 0) {
                    if (userDao.deleteUser(user.getId()) > 0) {
                        setUserInfo(userInfo, "Op:delete", 1,
                                new HashMap<>() {{
                                    put("注销 User 信息", new UserDTO(user, role));
                                }});
                        logger.info("UserEmail: {} 已成功删除", email);
                    } else {
                        setUserInfo(userInfo, "Op:delete", -1,
                                "删除 User ByEmail", "User 表删除失败");
                        logger.error("Cause: User 表删除失败! UserEmail: {} 仍存在于 User 表中", email);
                    } // user
                } else {
                    setUserInfo(userInfo, "Op:delete", -1,
                            "删除 UserRole 表 ByEmail", "UserRole 表删除失败");
                    logger.error("Cause: UserRole 表删除失败! UserEmail: {} 仍存在于 User 表和 UserRole 表中", email);
                } // userRole
            } else {
                setUserInfo(userInfo, "Op:delete", -1,
                        "Error", "角色获取异常");
                logger.error("Cause: 角色获取异常 DeleteByEmail");
            }
        } else {
            setUserInfo(userInfo, "Op:delete", 0,
                    "删除 User 表操作 ByEmail", "用户不存在");
            logger.error("Cause: User 表中不存在 UserEmail: {} 的用户", email);
        } // 不存在
        return null;
    }

    // 更新信息
    @Override
    public UserInfo updateUserInfo(User user) {
        UserInfo userInfo = new UserInfo();
        Integer id = user.getId();

        if (isExists(id)) {
            User srcUser = userDao.selectByID(id);
            String[] role = getRoles(id);
            if (role.length > 0) {
                if (userDao.updateUser(user) > 0) {
                    setUserInfo(userInfo, "Op:update", 1,
                            new HashMap<>() {{
                                put("sourceUser", new UserDTO(srcUser, role));
                                put("updateUser", new UserDTO(user, role));
                            }});
                    logger.info("UserID: {} 已更新", id);
                } else {
                    setUserInfo(userInfo, "Op:update", -1,
                            "更新 User", "更新失败");
                    logger.error("Cause: UserID: {} 更新失败", id);
                } // user
            } else {
                setUserInfo(userInfo, "Op:update", -1,
                        "Error", "角色获取异常");
                logger.error("Cause: 角色获取异常 UpdateUser");
            }
        } else {
            setUserInfo(userInfo, "Op:update", 0,
                    "更新 User", "用户不存在");
            logger.error("Cause: UserID: {} 不存在", id);
        } // 不存在
        return userInfo;
    }

    @Override
    public UserInfo updateUserInfo(User user, String role) {
        UserInfo userInfo = new UserInfo();
        Integer id = user.getId();

        if (isExists(id)) {
            User srcUser = userDao.selectByID(id);
            String[] srcRoles = getRoles(id);

            if (srcRoles.length > 0) {
                Integer r = roleDao.selectByName(role);
                if (r <= 0 || r > 3) {
                    setUserInfo(userInfo, "Op:update", 0,
                            "查询 Role", "role 不存在");
                    logger.error("Cause: Role: {} 不存在", role);
                } else {
                    if (userRoleDao.accurateSelect(new UserRole(user.getId(), r)) == 0) {
                        if (userDao.updateUser(user) > 0) {
                            if (userRoleDao.addNewUser(new UserRole(id, r)) > 0) {
                                setUserInfo(userInfo, "Op:update", 1,
                                        new HashMap<>() {{
                                            put("srcUser", new UserDTO(srcUser, srcRoles));
                                            put("updateUser", new UserDTO(user, getRoles(id)));
                                        }});
                                logger.info("User: id {} & UserRole: role {} 表更新成功", id, role);
                            } else {
                                logger.error("Cause: 更新 UserID: {} Role: {} 时, 插入 UserRole 失败, User 更新成功", id, role);
                                int reset = userDao.updateUser(srcUser);
                                if (reset > 0) {
                                    logger.info("User 重置成功");
                                } else {
                                    logger.error("User 重置失败");
                                }
                                setUserInfo(userInfo, "Op:update", -1,
                                        "插入 UserRole 表",
                                        "插入失败".concat(reset > 0 ? "" : ", 但 User 已更新"));
                            } // userRole update
                        } else {
                            setUserInfo(userInfo, "Op:update", -1,
                                    "更新 User", "更新失败");
                            logger.error("Cause: 更新 UserID: {} Role: {} 时, 更新 User 失败", id, role);
                        } // user update
                    } else {
                        setUserInfo(userInfo, "Op:update", 0,
                                "添加 Role", "role 已存在");
                        logger.error("Cause: UserID: {} 添加 Role: {} 时, User 已有 Role 角色", id, role);
                    }
                } // role
            } else {
                setUserInfo(userInfo, "Op:update", -1,
                        "Error", "角色获取异常");
                logger.error("Cause: 角色获取异常 updateUser&Role");
            }
        } else {
            setUserInfo(userInfo, "Op:update", 0,
                    "更新 User", "用户不存在");
            logger.error("Cause: UserID: {} 不存在", id);
        } // 不存在
        return userInfo;
    }

    @Override
    public UserInfo updateUserRole(Integer id, String role) {
        UserInfo userInfo = new UserInfo();

        if (isExists(id)) {
            String[] srcRoles = getRoles(id);

            if (srcRoles.length > 0) {
                Integer r = roleDao.selectByName(role);
                if (r <= 0 || r > 3) {
                    setUserInfo(userInfo, "Op:update", 0,
                            "Cause: 查询 Role", "role 不存在");
                    logger.error("Cause: Role: {} 不存在", role);
                } else {
                    if (userRoleDao.accurateSelect(new UserRole(id, r)) == 0) {
                        if (userRoleDao.addNewUser(new UserRole(id, r)) > 0) {
                            User user = userDao.selectByID(id);
                            setUserInfo(userInfo, "Op:update", 1,
                                    new HashMap<>() {{
                                        put("srcUser", new UserDTO(user, srcRoles));
                                        put("updateUser", new UserDTO(user, getRoles(id)));
                                    }});
                            logger.info("UserID: {} 添加 Role: {} 成功", id, role);
                        } else {
                            setUserInfo(userInfo, "Op:update", -1,
                                    "插入 UserRole ", "插入失败");
                            logger.error("Cause: UserID: {} 添加 Role: {} 失败", id, role);
                        } // userRole
                    } else {
                        setUserInfo(userInfo, "Op:update", 0,
                                "添加 Role", "role 已存在");
                        logger.error("Cause: UserID: {} 添加 Role: {} 时, User 已有 Role 角色", id, role);
                    }
                } // role
            } else {
                setUserInfo(userInfo, "Op:update", -1,
                        "Error", "角色获取异常");
                logger.error("Cause: 角色获取异常 updateRole");
            }
        } else {
            setUserInfo(userInfo, "Op:update", 0,
                    "添加 Role", "用户不存在");
            logger.error("Cause: UserID: {} 添加 Role: {} 时失败, User 不存在", id, role);
        }
        return userInfo;
    }

    @Override
    public UserInfo deleteUserRole(Integer id, String role) {
        UserInfo userInfo = new UserInfo();

        // 用户是否存在
        if (isExists(id)) {
            String[] roles = getRoles(id);
            // 角色查询是否成功
            if (roles.length > 0) {
                Integer r = roleDao.selectByName(role);
                // 需要删除的角色是否正确
                if (r > 0 && r <= 3) {
                    UserRole userRole = new UserRole(id, r);
                    // 用户有该角色时, 才能删除
                    if (userRoleDao.accurateSelect(userRole) == 1 &&
                            userRoleDao.accurateDelete(userRole) > 0) {
                        User user = userDao.selectByID(id);
                        setUserInfo(userInfo, "Op:update", 1,
                                new HashMap<>() {{
                                    put("srcUser", new UserDTO(user, roles));
                                    put("updateRole", new UserDTO(user, getRoles(id)));
                                }});
                        logger.info("UserID: {} 删除 Role: {} 成功", id, role);
                    } else {
                        setUserInfo(userInfo, "Op:update", -1,
                                "删除 UserRole", "删除失败");
                        logger.error("Cause: UserID: {} 删除 Role: {} 失败", id, role);
                    } // userRole
                } else {
                    setUserInfo(userInfo, "Op:update", 0,
                            "查询 Role", "Role 不存在");
                    logger.error("Cause: Role {} 不存在", role);
                }
            } else {
                setUserInfo(userInfo, "Op:update", -1,
                        "Error", "角色获取异常");
                logger.error("Cause: 角色获取异常 deleteRole");
            }
            // 防止用户最后没有角色, 因此设为默认角色
            if (userRoleDao.selectByUser(id).size() == 0) {
                userRoleDao.addNewUser(new UserRole(id, 1));
            }
        } else {
            setUserInfo(userInfo, "Op:update", 0,
                    "删除 Role", "用户不存在");
            logger.error("UserID: {} 删除 Role: {} 失败", id, role);
        } // 不存在
        return userInfo;
    }
}
