package cn.summer.homework.service.impl;

import cn.summer.homework.DTO.CourseSTDTO;
import cn.summer.homework.feignClient.CourseClient;
import cn.summer.homework.service.CourseSelectService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/6-23:57
 */

@Service
public class CourseSelectServiceImpl implements CourseSelectService {
    @Resource
    private CourseClient client;

    @Override
    public List<CourseSTDTO> getAll() {
        return client.getAll();
    }

    @Override
    public CourseSTDTO get(Integer id)
            throws IOException {
        if (id == null) {
            throw new IOException("输入 CourseID 为空");
        }
        return client.get(id);
    }

    @Override
    public List<CourseSTDTO> getByName(String name)
            throws IOException {
        if (name == null || name.equals("")) {
            throw new IOException("输入 Name 不符要求");
        }
        return client.getByName(name);
    }
}
