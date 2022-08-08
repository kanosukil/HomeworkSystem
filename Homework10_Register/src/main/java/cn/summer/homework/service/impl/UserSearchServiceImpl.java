package cn.summer.homework.service.impl;

import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.feignClient.UserClient;
import cn.summer.homework.service.UserSearchService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/5-13:34
 */

@Service
public class UserSearchServiceImpl implements UserSearchService {
    @Resource
    private UserClient client;

    @Override
    public List<UserRoleDTO> getAll() {
        return client.getAll();
    }

    @Override
    public UserRoleDTO get(Integer id)
            throws IOException {
        if (id == null) {
            throw new IOException("传入数据不能为空");
        }
        return client.get(id);
    }

    @Override
    public UserRoleDTO get(String email)
            throws IOException {
        if (email == null || email.equals("")) {
            throw new IOException("传入数据不能为空");
        }
        if (!email.matches("(.*)@(.*)\\.(.*)")) {
            throw new IOException("传入数据不符要求");
        }
        return client.login(email);
    }
}
