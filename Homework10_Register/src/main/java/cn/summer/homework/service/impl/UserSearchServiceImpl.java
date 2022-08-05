package cn.summer.homework.service.impl;

import cn.summer.homework.DTO.UserRoleDTO;
import cn.summer.homework.feignClient.UserClient;
import cn.summer.homework.service.UserSearchService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
    public UserRoleDTO get(Integer id) {
        return client.get(id);
    }
}
