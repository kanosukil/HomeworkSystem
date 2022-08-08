package cn.summer.homework.controller;

import cn.summer.homework.DTO.CourseSTDTO;
import cn.summer.homework.service.CourseSelectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/6-23:54
 */

@RestController
@RequestMapping("/query/course")
public class CourseFindController {
    @Resource
    private CourseSelectService service;

    @GetMapping("all")
    public List<CourseSTDTO> getAll() {
        return service.getAll();
    }

    @GetMapping("id")
    public CourseSTDTO get(@RequestParam("id") Integer id)
            throws IOException {
        return service.get(id);
    }

    @GetMapping("name")
    public List<CourseSTDTO> getn(@RequestParam("name") String name)
            throws IOException {
        return service.getByName(name);
    }

    @GetMapping("teacher")
    public List<CourseSTDTO> gett(@RequestParam("tid") Integer tid)
            throws IOException {
        return service.getByTeacher(tid);
    }

    @GetMapping("student")
    public List<CourseSTDTO> gets(@RequestParam("sid") Integer sid)
            throws IOException {
        return service.getByStudent(sid);
    }
}
