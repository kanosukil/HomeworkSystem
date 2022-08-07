package cn.summer.homework.controller;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.NewQuestionDTO;
import cn.summer.homework.DTO.NewResultDTO;
import cn.summer.homework.PO.TeacherQuestion;
import cn.summer.homework.service.QuestionIOService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.io.IOException;

/**
 * @author VHBin
 * @date 2022/8/6-22:13
 */

@RestController
@RequestMapping("create")
public class QuestionHandleController {
    @Resource
    private QuestionIOService service;

    @PostMapping("new")
    public HomeworkOpBO createNew(@RequestBody NewQuestionDTO newQuestion) {
        return service.createNewQuestion(newQuestion);
    }

    @PostMapping("update")
    public HomeworkOpBO update(@RequestBody NewQuestionDTO updateQuestion) {
        return service.updateQuestion(updateQuestion);
    }

    @PostMapping("delete")
    public HomeworkOpBO delete(@RequestBody TeacherQuestion tq) {
        return service.deleteQuestion(tq);
    }

    @PostMapping("correct")
    public HomeworkOpBO correct(@RequestBody NewResultDTO correctResult) {
        return service.correctResult(correctResult);
    }

    @PostMapping("/new/type")
    public Boolean createT(@RequestBody NewQuestionDTO qt)
            throws IOException {
        return service.createType(qt);
    }

    @PostMapping("/delete/type")
    public Boolean deleteT(@RequestBody NewQuestionDTO qt)
            throws IOException {
        return service.deleteType(qt);
    }
}
