package cn.summer.homework.service;

import cn.summer.homework.DTO.CourseSTDTO;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/15-18:26
 */
public interface CourseSelectService {
    List<CourseSTDTO> getAll();

    CourseSTDTO get(Integer id);

    List<CourseSTDTO> getByName(String name);
}
