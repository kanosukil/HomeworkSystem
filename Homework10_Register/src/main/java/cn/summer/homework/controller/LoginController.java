package cn.summer.homework.controller;

import cn.summer.homework.DTO.LoginDTO;
import cn.summer.homework.DTO.UserDTO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.Entity.User;
import cn.summer.homework.Util.TokenUtil;
import cn.summer.homework.VO.UserVO;
import cn.summer.homework.service.UserIOService;
import cn.summer.homework.service.UserSearchService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
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
    @Resource
    private UserSearchService userSearchService;

    @PostMapping("login")
    public UserVO<String> login(@RequestBody LoginDTO login) {
        if (login == null || login.getAccount() == null || login.getPassword() == null) {
            return new UserVO<>(400, "登录数据为空", null);
        }
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

    }
}
