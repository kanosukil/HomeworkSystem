package cn.summer.homework.service.impl;

import cn.summer.homework.DTO.CourseSTDTO;
import cn.summer.homework.feignClient.CourseClient;
import cn.summer.homework.service.CourseSelectService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
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
        return null;
    }

    @Override
    public CourseSTDTO get(Integer id) {
        return null;
    }

    @Override
    public List<CourseSTDTO> getByName(String name) {
        return null;
    }
}
