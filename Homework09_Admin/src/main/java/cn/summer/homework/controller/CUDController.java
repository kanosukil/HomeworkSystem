package cn.summer.homework.controller;

import cn.summer.homework.DTO.CourseInDTO;
import cn.summer.homework.DTO.QuestionInDTO;
import cn.summer.homework.DTO.ResultInDTO;
import cn.summer.homework.VO.AdminVO;
import cn.summer.homework.VO.StudentVO;
import cn.summer.homework.VO.TeacherVO;
import cn.summer.homework.feignClient.StudentClient;
import cn.summer.homework.feignClient.TeacherClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/8/8-17:12
 */

@RestController
@RequestMapping("a")
public class CUDController {
    @Resource
    private StudentClient student;
    @Resource
    private TeacherClient teacher;

    private AdminVO get(TeacherVO teacher) {
        return new AdminVO(teacher.getCode(), teacher.getMsg(), teacher.getInfo());
    }

    private AdminVO get(StudentVO student) {
        return new AdminVO(student.getCode(), student.getMsg(), student.getInfo());
    }

    /*
        create
     */
    @PostMapping("/c/course")
    public AdminVO createCourse(@RequestBody CourseInDTO in) {
        return get(teacher.createCourse(in));
    }

    @PostMapping("/c/question")
    public AdminVO createQuestion(@RequestBody QuestionInDTO in) {
        return get(teacher.createQuestion(in));
    }

    @PostMapping("/c/type")
    public AdminVO createType(@RequestBody QuestionInDTO in) {
        return get(teacher.createType(in));
    }

    @PostMapping("/c/result")
    public AdminVO createResult(@RequestBody ResultInDTO in) {
        return get(student.createResult(in));
    }

    /*
        update
     */
    @PostMapping("/u/course")
    public AdminVO updateCourse(@RequestBody CourseInDTO in) {
        return get(teacher.updateCourse(in));
    }

    @PostMapping("/u/question")
    public AdminVO updateQuestion(@RequestBody QuestionInDTO in) {
        return get(teacher.updateQuestion(in));
    }

    @PostMapping("/u/result")
    public AdminVO updateResult(@RequestBody ResultInDTO in) {
        return get(student.updateResult(in));
    }

    /*
        delete
     */
    @PostMapping("/d/course")
    public AdminVO deleteCourse(@RequestBody CourseInDTO in) {
        return get(teacher.deleteCourse(in));
    }

    @PostMapping("/d/question")
    public AdminVO deleteQuestion(@RequestBody QuestionInDTO in) {
        return get(teacher.deleteQuestion(in));
    }

    @PostMapping("/d/type")
    public AdminVO deleteType(@RequestBody QuestionInDTO in) {
        return get(teacher.deleteType(in));
    }

    @PostMapping("/d/result")
    public AdminVO deleteResult(@RequestBody ResultInDTO in) {
        return get(student.deleteResult(in));
    }
}
