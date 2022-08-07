package cn.summer.homework.controller;

import cn.summer.homework.BO.UserOpBO;
import cn.summer.homework.DTO.LoginDTO;
import cn.summer.homework.DTO.RegisterDTO;
import cn.summer.homework.DTO.UserDTO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.Entity.User;
import cn.summer.homework.Util.TokenUtil;
import cn.summer.homework.VO.UserVO;
import cn.summer.homework.service.UserIOService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
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
    @Resource
    private UserIOService userIOService;

    @PostMapping("login")
    public UserVO<String> login(@RequestBody LoginDTO login) {
        if (login == null || login.getAccount() == null || login.getPassword() == null) {
            return new UserVO<>(400, "登录数据为空", null);
        }
        try {
            UserRoleDTO lo = userIOService.login(login.getAccount());
            User user = lo.getUser();
            List<String> roles = lo.getRoles();
            AtomicBoolean isAdmin = new AtomicBoolean(false);
            if (user == null || roles == null) {
                return new UserVO<>(400, "未找到指定用户", "");
            }
            roles.forEach(e -> {
                if (e.equals("Admin")) {
                    isAdmin.set(true);
                }
            });
            if (!Base64.getEncoder().encodeToString(login.getPassword().getBytes(StandardCharsets.UTF_8))
                    .equals(user.getPassword_hash())) {
                return new UserVO<>(200, "密码错误", "");
            } else {
                return new UserVO<>(200, "登录成功", isAdmin.get(),
                        TokenUtil.generateJWToken(
                                new UserDTO(user.getId(), user.getEmail(), user.getPassword_hash()),
                                roles));
            }
        } catch (Exception ex) {
            return new UserVO<>(500, "登录异常", ex.getMessage());
        }

    }

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
        ArrayList<String> role = new ArrayList<>();
        role.add("Student");
        UserOpBO reg = userIOService.register(new UserRoleDTO(newUser, role));
        if (!reg.getIsSuccess()) {
            return new UserVO<>(400, reg.getInfo().get("Cause").toString(), "");
        } else {
            return new UserVO<>(200, "注册成功", TokenUtil.generateJWToken(
                    new UserDTO(
                            Integer.parseInt(reg.getInfo().get("uid").toString()),
                            newUser.getName(),
                            newUser.getPassword_hash()), role));
        }
    }
}
