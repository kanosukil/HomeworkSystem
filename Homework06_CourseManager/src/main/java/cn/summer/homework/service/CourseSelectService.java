package cn.summer.homework.service;

import cn.summer.homework.DTO.CourseSTDTO;

import java.io.IOException;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/15-18:26
 */
public interface CourseSelectService {
    List<CourseSTDTO> getAll();

    CourseSTDTO get(Integer id) throws IOException;

    List<CourseSTDTO> getByName(String name) throws IOException;

    List<CourseSTDTO> getByTeacher(Integer tid) throws IOException;

    List<CourseSTDTO> getByStudent(Integer sid) throws IOException;
}
