package cn.summer.homework.service.impl;

import cn.summer.homework.BO.UserOpBO;
import cn.summer.homework.DTO.URoleDTO;
import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.Entity.User;
import cn.summer.homework.Util.OpBOUtil;
import cn.summer.homework.feignClient.UserClient;
import cn.summer.homework.service.UserIOService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author VHBin
 * @date 2022/8/5-13:26
 */

@Service
public class UserIOServiceImpl implements UserIOService {

    @Resource
    private UserClient client;

    @Override
    public UserOpBO register(UserRoleDTO newUser) {
        return client.register(newUser);
    }

    @Override
    public UserRoleDTO login(String email)
            throws IOException {
        if (email == null || email.equals("") ||
                !email.matches("(.*)@(.*)\\.(.*)")) {
            throw new IOException("Email 格式不符/内容为空");
        }
        return client.login(email);
    }

    @Override
    public UserOpBO delete(Integer id) {
        if (id == null) {
            return OpBOUtil.generateUOB("UserID 为空");
        }
        return client.delete(id);
    }

    @Override
    public UserOpBO logoff(String email) {
        if (email == null || email.equals("") ||
                !email.matches("(.*)@(.*)\\.(.*)")) {
            return OpBOUtil.generateUOB("Email 格式不符/内容为空");
        }
        return client.logoff(email);
    }

    @Override
    public UserOpBO update(UserRoleDTO updateUser) {
        User user = updateUser.getUser();
        if (user == null || updateUser.getRoles() == null ||
                updateUser.getRoles().size() == 0) {
            return OpBOUtil.generateUOB("用户更新传入数据不能为空");
        }
        if (IsUserNull(user)) {
            return OpBOUtil.generateUOB("用户更新的数据无效");
        }
        if (user.getCreate_time().before(client.get(user.getId())
                .getUser().getCreate_time())) {
            return OpBOUtil.generateUOB("用户更新的创建时间错误");
        }
        return client.update(updateUser);
    }

    @Override
    public UserOpBO infoUpdate(User user) {
        if (user == null) {
            return OpBOUtil.generateUOB("更新用户数据为空");
        }
        if (IsUserNull(user)) {
            return OpBOUtil.generateUOB("用户更新的数据无效");
        }
        return client.infoUpdate(user);
    }

    private boolean IsUserNull(User user) {
        return user.getId() == null || user.getEmail().equals("")
                || !user.getEmail().matches("(.*)@(.*)\\.(.*)")
                || user.getEmail() == null || user.getName().equals("")
                || user.getName().matches("\\w{16}") || user.getName() == null
                || user.getCreate_time() == null || user.getPassword_hash().equals("")
                || user.getPassword_hash() == null || user.getHead_image() == null
                || user.getIntroduction() == null;
    }

    @Override
    public UserOpBO roleUpdate(URoleDTO roles) {
        if (roles.getUid() == 0 || roles.getRoles() == null || roles.getRoles().size() == 0) {
            return OpBOUtil.generateUOB("用户角色更新传入数据无效");
        }
        return client.roleUpdate(roles);
    }
}
