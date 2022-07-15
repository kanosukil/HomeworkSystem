package cn.summer.homework.feignClient;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.NewQuestionDTO;
import cn.summer.homework.DTO.NewResultDTO;
import cn.summer.homework.DTO.QuestionResultDTO;
import cn.summer.homework.PO.TeacherQuestion;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/15-15:03
 */

@FeignClient("HomeworkSQL")
public interface QuestionClient {
    @GetMapping("/question/questions-get")
    List<QuestionResultDTO> getAQuestion();

    @GetMapping("/question/question-get")
    QuestionResultDTO getQuestion(@RequestParam("qid") Integer qid);

    @GetMapping("/question/questions-get-tid")
    List<QuestionResultDTO> getTeacherQuestion(@RequestParam("tid") Integer tid);

    @GetMapping("/question/question-get-type")
    List<QuestionResultDTO> getTypeQuestion(@RequestParam("type") String type);

    @GetMapping("/question/question-get-course")
    List<QuestionResultDTO> getCourseQuestion(@RequestParam("cid") Integer cid);

    @PostMapping("/question/question-create")
    HomeworkOpBO createNewQuestion(@RequestBody NewQuestionDTO newQuestion);

    @PostMapping("/question/question-update")
    HomeworkOpBO updateQuestion(@RequestBody NewQuestionDTO updateQuestion);

    @PostMapping("/question/question-delete")
    HomeworkOpBO deleteQuestion(@RequestBody TeacherQuestion tq);

    @PostMapping("/question/result-correct")
    HomeworkOpBO correctResult(@RequestBody NewResultDTO correctResult);

    @GetMapping("/question/question-types-get")
    List<String> getAType();

    @PostMapping("/question/question-type-add")
    Boolean createType(@RequestBody NewQuestionDTO questionType);

    @PostMapping("/question/question-type-delete")
    Boolean deleteType(@RequestBody NewQuestionDTO questionType);
}
