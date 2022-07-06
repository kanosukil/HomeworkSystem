package cn.summer.homework.service.impl;

import cn.summer.homework.DTO.UserOpDTO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.Entity.User;
import cn.summer.homework.PO.UserRole;
import cn.summer.homework.dao.RoleDao;
import cn.summer.homework.dao.UserDao;
import cn.summer.homework.dao.cascade.UserRoleDao;
import cn.summer.homework.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.Nonnull;
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
        // 获取 UserRole 信息
        List<Integer> roles = userRoleDao.selectByUser(id);
        // 获取 Role: Student id
        Integer student = roleDao.selectByName("Student");
        // 比对 UserRole 信息中是否有 Role: Student
        for (Integer role : roles) {
            if (role.equals(student))
                return true;
        }
        return false;
    }

    // role->userRole==id
    @Override
    public boolean isTeacher(Integer id) {
        // 获取 Role: Teacher id
        Integer teacher = roleDao.selectByName("Teacher");
        // 获取 UserRole 信息
        List<Integer> users = userRoleDao.selectByRole(teacher);
        // 比对 UserRole 中是否有 Role: Teacher id
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
        // 获取 UserRole 信息
        List<Integer> roles = userRoleDao.selectByUser(id);
        // 直接比对
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

    @Override
    public List<User> getAllUser() {
        return userDao.selectAll();
    }

    @Override
    public List<User> findUser(String username) {
        return userDao.selectByName(username);
    }

    @Override
    public User findUser(Integer id) {
        return userDao.selectByID(id);
    }

    @Override
    public User findUserByEmail(String email) {
        return userDao.selectByEmail(email);
    }

    private void setUserOpDTO(@Nonnull UserOpDTO userOpDTO,
                              String key, String value) {
        userOpDTO.setIsSuccess(false);
        userOpDTO.setInfo(new HashMap<>() {{
            put(key, value);
        }});

    }

    private void setUserOpDTO(@Nonnull UserOpDTO userOpDTO,
                              Map<String, Object> map) {
        userOpDTO.setIsSuccess(true);
        userOpDTO.setInfo(map);
    }

    private List<String> getRoles(Integer uid) {
        List<String> res = new ArrayList<>();
        userRoleDao.selectByUser(uid).forEach(e ->
                res.add(roleDao.selectByID(e)));
        return res;
    }

    @Override
    public UserOpDTO createNewUser(User user, String role) {
        UserOpDTO userOpDTO = new UserOpDTO();
        int[] insert = new int[2];
        Integer rid;
        Integer uid = 0;
        int flag = 0;
        try {
            rid = roleDao.selectByName(role);
            if (rid <= 0) {
                throw new Exception("查询Role异常: Role=".concat(role));
            }
            flag = 1;
            insert[0] = userDao.insertNewUser(user);
            User finalUser = userDao.selectByEmail(user.getEmail());
            uid = finalUser.getId();
            flag = 2;
            insert[1] = userRoleDao.addNewUser(
                    new UserRole(uid, rid));
            flag = 3;
            logger.info("新建 UserID: {} 成功", uid);
            logger.info("User 表成功插入 {} 条数据", insert[0]);
            logger.info("UserRole 表成功插入 {} 条数据", insert[1]);
            Integer finalUid = uid;
            setUserOpDTO(userOpDTO, new HashMap<>() {{
                put("新增用户",
                        new UserRoleDTO(finalUser, getRoles(finalUid)));
            }});
        } catch (Exception ex) {
            logger.error("UserID: {} 插入异常", uid);
            switch (flag) {
                case 1 -> logger.error("User 插入失败: {}", ex.getMessage());
                case 2 -> {
                    logger.error("UserRole 插入失败: {}", ex.getMessage());
                    if (uid > 0) {
                        int i = userDao.deleteUser(uid);
                        if (i <= 0) {
                            logger.error("UserID: {} 新增 User 删除失败", uid);
                        }
                    }
                }
                case 0 -> logger.error("Role 查询异常: {}", ex.getMessage());
                default -> logger.error("未知异常: {}", ex.getMessage());
            }
            setUserOpDTO(userOpDTO, "新建 User 失败", ex.getMessage());
        }
        return userOpDTO;
    }

    private void deleteExLog(UserOpDTO userOpDTO, int flag, Exception ex) {
        switch (flag) {
            case 0 -> logger.error("User 查询, 用户不存在: {}", ex.getMessage());
            case 1 -> logger.error("UserRole 删除失败: {}", ex.getMessage());
            case 2 -> logger.error("User 删除失败: {}", ex.getMessage());
            default -> logger.error("未知错误: {}", ex.getMessage());
        }
        setUserOpDTO(userOpDTO, "删除 User 失败", ex.getMessage());
    }

    @Override
    public UserOpDTO deleteUser(Integer id) {
        UserOpDTO userOpDTO = new UserOpDTO();
        int[] delete = new int[2];
        int flag = 0;
        try {
            if (isExists(id)) {
                throw new Exception("用户不存在. UserID=".concat(id.toString()));
            }
            flag = 1;
            List<String> roles = getRoles(id);
            delete[0] = userRoleDao.deleteUser(id);
            flag = 2;
            User user = userDao.selectByID(id);
            delete[1] = userDao.deleteUser(id);
            flag = 3;
            logger.info("删除 UserID: {} 成功", id);
            logger.info("User 表删除 {} 条数据", delete[1]);
            logger.info("UserRole 表删除 {} 条数据", delete[0]);
            setUserOpDTO(userOpDTO, new HashMap<>() {{
                put("删除用户", new UserRoleDTO(user, roles));
            }});
        } catch (Exception ex) {
            logger.error("UserID: {} 删除异常", id);
            deleteExLog(userOpDTO, flag, ex);
        }
        return userOpDTO;
    }

    @Override
    public UserOpDTO deleteUser(String email) {
        UserOpDTO userOpDTO = new UserOpDTO();
        int[] delete = new int[2];
        int flag = 0;
        try {
            if (isEmailUsed(email)) {
                throw new Exception("用户不存在. UserEmail=".concat(email));
            }
            User user = userDao.selectByEmail(email);
            Integer id = user.getId();
            flag = 1;
            List<String> roles = getRoles(id);
            delete[0] = userRoleDao.deleteUser(id);
            flag = 2;
            delete[1] = userDao.deleteUser(id);
            flag = 3;
            logger.info("删除 UserID: {} 成功", id);
            logger.info("User 表删除 {} 条数据", delete[1]);
            logger.info("UserRole 表删除 {} 条数据", delete[0]);
            setUserOpDTO(userOpDTO, new HashMap<>() {{
                put("删除用户", new UserRoleDTO(user, roles));
            }});
        } catch (Exception ex) {
            logger.error("UserEmail: {} 删除异常", email);
            deleteExLog(userOpDTO, flag, ex);
        }
        return userOpDTO;
    }

    private void updateExLog(UserOpDTO userOpDTO, int flag, Exception ex) {
        switch (flag) {
            case 0 -> logger.error("查询异常: {}", ex.getMessage());
            case 1 -> logger.error("User 表更新异常: {}", ex.getMessage());
            case 2 -> logger.error("UserRole 表更新异常: {}", ex.getMessage());
            default -> logger.error("未知异常: {}", ex.getMessage());
        }
        setUserOpDTO(userOpDTO, "更新 User 失败", ex.getMessage());
    }

    @Override
    public UserOpDTO updateUserInfo(User user) {
        UserOpDTO userOpDTO = new UserOpDTO();
        Integer id = user.getId();
        int flag = 0;
        try {
            if (isExists(id)) {
                throw new Exception("用户不存在. UserID=".concat(id.toString()));
            }
            User srcUser = userDao.selectByID(id);
            flag = 1;
            int update = userDao.updateUser(user);
            flag = 2;
            logger.info("UserID: {} 更新完成", id);
            logger.info("User 表更新 {} 条数据", update);
            List<String> roles = getRoles(id);
            setUserOpDTO(userOpDTO, new HashMap<>() {{
                put("SrcUser", new UserRoleDTO(srcUser, roles));
                put("UpdateUser", new UserRoleDTO(user, roles));
            }});
        } catch (Exception ex) {
            logger.error("UserID: {} 更新异常", id);
            updateExLog(userOpDTO, flag, ex);
        }
        return userOpDTO;
    }

    @Override
    public UserOpDTO updateUserInfo(User user, String role) {
        UserOpDTO userOpDTO = new UserOpDTO();
        Integer uid = user.getId();
        Integer rid;
        int flag = 0;
        int[] update = new int[2];
        try {
            if (isExists(uid)) {
                throw new Exception("用户不存在. UserID=".concat(uid.toString()));
            }
            if ((rid = roleDao.selectByName(role)) <= 0) {
                throw new Exception("需要添加的角色不存在. Role=".concat(role));
            }
            User srcUser = userDao.selectByID(uid);
            flag = 1;
            update[0] = userDao.updateUser(user);
            flag = 2;
            List<String> srcRoles = getRoles(uid);
            update[1] = userRoleDao.addNewUser(new UserRole(uid, rid));
            flag = 3;
            logger.info("UserID: {} 更新完成", uid);
            logger.info("User 表更新 {} 条数据", update[0]);
            logger.info("UserRole 表更新 {} 条数据", update[1]);
            setUserOpDTO(userOpDTO, new HashMap<>() {{
                put("SrcUser", new UserRoleDTO(srcUser, srcRoles));
                put("UpdateUser", new UserRoleDTO(user, getRoles(uid)));
            }});
        } catch (Exception ex) {
            logger.error("UserID: {} 更新异常", uid);
            updateExLog(userOpDTO, flag, ex);
        }
        return userOpDTO;
    }

    @Override
    public UserOpDTO updateUserRole(Integer id, String role) {
        UserOpDTO userOpDTO = new UserOpDTO();
        int flag = 0;
        Integer rid;
        try {
            if (isExists(id)) {
                throw new Exception("用户不存在. UserID=".concat(id.toString()));
            }
            if ((rid = roleDao.selectByName(role)) <= 0) {
                throw new Exception("需要添加的角色不存在. Role=".concat(role));
            }
            if (userRoleDao.accurateSelect(new UserRole(id, rid)) > 0) {
                throw new Exception("用户已有该角色. UserID=" + id + " Role=" + role);
            }
            List<String> srcRoles = getRoles(id);
            flag = 2;
            int update = userRoleDao.addNewUser(new UserRole(id, rid));
            flag = 3;
            logger.info("UserID: {} 更新完成", id);
            logger.info("UserRole 表更新 {} 条数据", update);
            User user = userDao.selectByID(id);
            setUserOpDTO(userOpDTO, new HashMap<>() {{
                put("SrcUser", new UserRoleDTO(user, srcRoles));
                put("UpdateUser", new UserRoleDTO(user, getRoles(id)));
            }});
        } catch (Exception ex) {
            logger.error("UserID: {} 更新异常", id);
            updateExLog(userOpDTO, flag, ex);
        }
        return userOpDTO;
    }

    @Override
    public UserOpDTO deleteUserRole(Integer id, String role) {
        UserOpDTO userOpDTO = new UserOpDTO();
        int flag = 0;
        Integer rid;
        try {
            if (isExists(id)) {
                throw new Exception("用户不存在. UserID=".concat(id.toString()));
            }
            if ((rid = roleDao.selectByName(role)) <= 0) {
                throw new Exception("需要删除的角色不存在. Role=".concat(role));
            }
            if (userRoleDao.accurateSelect(new UserRole(id, rid)) <= 0) {
                throw new Exception("用户已有该角色. UserID=" + id + " Role=" + role);
            }
            List<String> srcRoles = getRoles(id);
            flag = 2;
            int update = userRoleDao.accurateDelete(new UserRole(id, rid));
            flag = 3;
            logger.info("UserID: {} 更新完成", id);
            logger.info("UserRole 表更新 {} 条数据", update);
            User user = userDao.selectByID(id);
            setUserOpDTO(userOpDTO, new HashMap<>() {{
                put("SrcUser", new UserRoleDTO(user, srcRoles));
                put("UpdateUser", new UserRoleDTO(user, getRoles(id)));
            }});
        } catch (Exception ex) {
            logger.error("UserID: {} 更新异常", id);
            updateExLog(userOpDTO, flag, ex);
        }
        return userOpDTO;
    }
}
