package cn.summer.homework.controller;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.NewQuestionDTO;
import cn.summer.homework.DTO.NewResultDTO;
import cn.summer.homework.DTO.QuestionResultDTO;
import cn.summer.homework.PO.TeacherQuestion;
import cn.summer.homework.feignClient.HomeworkSQLClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/14-23:59
 */

@RefreshScope
@RestController
@RequestMapping("question")
public class QuestionServiceController {
    @Resource
    private HomeworkSQLClient sqlClient;

    @GetMapping("questions-get")
    public List<QuestionResultDTO> getAQuestion() {
        return sqlClient.getAQuestion();
    }

    @GetMapping("question-get")
    public QuestionResultDTO getQuestion(@RequestParam("qid") Integer qid) {
        return sqlClient.getQuestion(qid);
    }

    @GetMapping("questions-get-tid")
    public List<QuestionResultDTO> getTeacherQuestion(@RequestParam("tid") Integer tid) {
        return sqlClient.getTeacherQuestion(tid);
    }

    @GetMapping("question-get-type")
    public List<QuestionResultDTO> getTypeQuestion(@RequestParam("type") String type) {
        return sqlClient.getTypeQuestion(type);
    }

    @GetMapping("question-get-course")
    public List<QuestionResultDTO> getCourseQuestion(@RequestParam("cid") Integer cid) {
        return sqlClient.getCourseQuestion(cid);
    }

    @PostMapping("question-create")
    public HomeworkOpBO createNewQuestion(@RequestBody NewQuestionDTO newQuestion) {
        return sqlClient.createNewQuestion(newQuestion);
    }

    @PostMapping("question-update")
    public HomeworkOpBO updateQuestion(@RequestBody NewQuestionDTO updateQuestion) {
        return sqlClient.updateQuestion(updateQuestion);
    }

    @PostMapping("question-delete")
    public HomeworkOpBO deleteQuestion(@RequestBody TeacherQuestion tq) {
        return sqlClient.deleteQuestion(tq);
    }

    @PostMapping("result-correct")
    public HomeworkOpBO correctResult(@RequestBody NewResultDTO correctResult) {
        return sqlClient.correctResult(correctResult);
    }

    @GetMapping("question-types-get")
    public List<String> getAType() {
        return sqlClient.getAType();
    }

    @PostMapping("question-type-add")
    public Boolean createType(@RequestBody NewQuestionDTO questionType) {
        return sqlClient.createType(questionType);
    }

    @PostMapping("question-type-delete")
    public Boolean deleteType(@RequestBody NewQuestionDTO questionType) {
        return sqlClient.deleteType(questionType);
    }
}
