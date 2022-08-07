package cn.summer.homework.controller;

import cn.summer.homework.DTO.QuestionResultDTO;
import cn.summer.homework.service.QuestionSelectService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/6-23:49
 */

@RestController
@RequestMapping("/query/question")
public class QuestionFindController {
    @Resource
    private QuestionSelectService service;

    @GetMapping("/all")
    public List<QuestionResultDTO> getAll() {
        return service.getAQuestion();
    }

    @GetMapping("/id")
    public QuestionResultDTO get(@RequestParam("id") Integer qid)
            throws IOException {
        return service.getQuestion(qid);
    }

    @GetMapping("/teacher")
    public List<QuestionResultDTO> getTeacherQuestion(@RequestParam("tid") Integer tid)
            throws IOException {
        return service.getTeacherQuestion(tid);
    }

    @GetMapping("/type")
    public List<QuestionResultDTO> getTypeQuestion(@RequestParam("type") String type)
            throws IOException {
        return service.getTypeQuestion(type);
    }

    @GetMapping("/course")
    public List<QuestionResultDTO> getCourseQuestion(@RequestParam("cid") Integer cid)
            throws IOException {
        return service.getCourseQuestion(cid);
    }

    @GetMapping("/all-type")
    public List<String> getAType() {
        return service.getAType();
    }
}
