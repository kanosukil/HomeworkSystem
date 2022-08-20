package cn.summer.homework.controller;

import cn.summer.homework.BO.UserOpBO;
import cn.summer.homework.DTO.*;
import cn.summer.homework.Entity.User;
import cn.summer.homework.Util.IndexUtil;
import cn.summer.homework.Util.TokenUtil;
import cn.summer.homework.VO.UserVO;
import cn.summer.homework.feignClient.ESCRUDClient;
import cn.summer.homework.service.ElasticSearchDirectExchangeService;
import cn.summer.homework.service.UserIOService;
import cn.summer.homework.service.UserSearchService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author VHBin
 * @date 2022/8/5-13:37
 */

@RestController
@RequestMapping("api")
public class LoginController {
    private static final Logger logger = LoggerFactory.getLogger(LoginController.class);
    @Resource
    private UserIOService userIOService;
    @Resource
    private UserSearchService userSearchService;
    @Resource
    private ElasticSearchDirectExchangeService ess;
    @Resource
    private ESCRUDClient es;

    /**
     * 登录
     *
     * @param login{user`s email, user`s password}
     * @return code, message, token/Cause of Error
     */
    @PostMapping("login")
    public UserVO<String> login(@RequestBody LoginDTO login) {
        if (login == null || login.getAccount() == null || login.getPassword() == null) {
            return new UserVO<>(400, "登录数据为空", null);
        }
        try {
            UserRoleDTO lo = userIOService.login(login.getAccount());
            User user = lo.getUser();
            StringBuilder roles = new StringBuilder();
            AtomicBoolean isAdmin = new AtomicBoolean(false);
            AtomicBoolean isTeacher = new AtomicBoolean(false);
            AtomicBoolean isStudent = new AtomicBoolean(false);
            lo.getRoles().forEach(e -> {
                roles.append(e).append(" ");
                switch (e) {
                    case "Admin" -> isAdmin.set(true);
                    case "Teacher" -> isTeacher.set(true);
                    case "Student" -> isStudent.set(true);
                }
            });
            if (user == null || roles.toString().equals("")) {
                return new UserVO<>(400, "未找到指定用户", "");
            }
            logger.info("Result: {}", lo);
            if (!Base64.getEncoder().encodeToString(login.getPassword().getBytes(StandardCharsets.UTF_8))
                    .equals(user.getPassword_hash())) {
                return new UserVO<>(400, "密码错误", "");
            } else {
                return new UserVO<>(200, "登录成功", isAdmin.get(), isTeacher.get(),
                        isStudent.get(), user.getId(),
                        TokenUtil.generateJWToken(
                                new UserDTO(user.getId(), user.getEmail()), roles.toString()));
            }
        } catch (Exception ex) {
            return new UserVO<>(500, "登录异常", ex.getMessage());
        } finally {
            ElasticSearchDTO search = new ElasticSearchDTO();
            search.setOption(7);
            search.setIndex(IndexUtil.USER);
            List<Integer> list = es.searchAll(search);
            int size = list.size();
            if ((size != 0 && list.get(0) != -1)
                    || size != userSearchService.getAll().size()) {
                deleteIndex(IndexUtil.USER);
            }
        }
    }

    private void deleteIndex(String index) {
        ElasticSearchDTO search = new ElasticSearchDTO();
        search.setOption(4);
        search.setIndex(index);
        if (es.indexDelete(search).getIsSuccess()) {
            logger.info("Index User 删除完成");
        } else {
            logger.error("Index User 删除异常");
        }
    }

    /**
     * 注册
     *
     * @param register{name, password, email}
     * @return code, message, token/Cause of Error
     */
    @PostMapping("register")
    public UserVO<String> register(@RequestBody RegisterDTO register) {
        String email = register.getEmail();
        if (register.getName() == null ||
                register.getPassword() == null || email == null) {
            return new UserVO<>(400, "注册数据为空", null);
        }
        User newUser = new User();
        newUser.setName(register.getName());
        newUser.setPassword_hash(
                Base64.getEncoder()
                        .encodeToString(
                                register.getPassword()
                                        .getBytes(StandardCharsets.UTF_8)));
        newUser.setEmail(register.getEmail());
        newUser.setCreate_time(new Date());
        UserOpBO reg = userIOService.register(new UserRoleDTO(newUser, new ArrayList<>(1) {{
            add("Student");
        }}));
        if (!reg.getIsSuccess()) {
            return new UserVO<>(400, reg.getInfo().get("Cause").toString(), "");
        } else {
            try {
                if (ess.save(userIOService.login(newUser.getEmail()))) {
                    logger.info("ES Save New User 完成");
                } else {
                    logger.warn("ES Save New User 异常");
                    deleteIndex(IndexUtil.USER);
                }
            } catch (IOException io) {
                logger.error("ES Save New User Exception: {}", io.getMessage());
                deleteIndex(IndexUtil.USER);
            }
            logger.info("Result: {}", reg);
            return new UserVO<>(200, "注册成功", false, false,
                    true, Integer.valueOf(reg.getInfo().get("uid").toString()),
                    TokenUtil.generateJWToken(
                            new UserDTO(
                                    Integer.parseInt(reg.getInfo().get("uid").toString()),
                                    newUser.getName()),
                            "Student"));
        }
    }

    @PostMapping("/logoff/id")
    public UserVO<String> logoff(@RequestParam("uid") Integer uid,
                                 HttpServletRequest request) {
        try {
            logger.info("Request: \n{}", request.getAttribute("userid"));
            if (uid != Integer.parseInt(request.getAttribute("userid").toString())) {
                throw new IOException("无效操作: 不允许操作其他用户");
            }
            UserRoleDTO user = userSearchService.get(uid);
            if (user == null) {
                throw new IOException("用户不存在");
            }
            return logoff(user.getUser().getEmail(), request);
        } catch (IOException io) {
            logger.error("User Logoff Exception: {}", io.getMessage());
            return new UserVO<>(500, "Cause", io.getMessage());
        }
    }

    @PostMapping("/logoff/email")
    public UserVO<String> logoff(@RequestParam("email") String email,
                                 HttpServletRequest request) {
        try {
            UserRoleDTO ur = userSearchService.get(email);
            if (ur == null || ur.getUser().getId() !=
                    Integer.parseInt(request.getAttribute("userid").toString())) {
                throw new IOException("无效操作: 不允许操作其他用户");
            }
            UserOpBO logoff = userIOService.logoff(email);
            if (logoff.getIsSuccess()) {
                if (ess.delete(ur)) {
                    logger.info("ES Delete User 完成");
                    deleteIndex(IndexUtil.COURSE);
                    deleteIndex(IndexUtil.QUESTION);
                    deleteIndex(IndexUtil.RESULT);
                } else {
                    logger.warn("ES Delete User 异常");
                    deleteIndex(IndexUtil.USER);
                }
                return new UserVO<>(200, "OK",
                        logoff.getInfo().toString());
            } else {
                return new UserVO<>(500, "cause",
                        logoff.getInfo().get("Cause").toString());
            }
        } catch (IOException e) {
            logger.error("Delete User Exception: {}", e.getMessage());
            deleteIndex(IndexUtil.USER);
            deleteIndex(IndexUtil.COURSE);
            deleteIndex(IndexUtil.QUESTION);
            deleteIndex(IndexUtil.RESULT);
            return new UserVO<>(500, "cause", e.getMessage());
        }
    }
}
