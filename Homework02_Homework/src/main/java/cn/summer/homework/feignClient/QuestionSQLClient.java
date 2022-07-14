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
 * @date 2022/7/14-23:25
 */

@FeignClient("SQLService")
public interface QuestionSQLClient {
    @GetMapping("/homework-sql/questions-get")
    List<QuestionResultDTO> getAQuestion();

    @GetMapping("/homework-sql/question-get")
    QuestionResultDTO getQuestion(@RequestParam("qid") Integer qid);

    @GetMapping("/homework-sql/questions-get-tid")
    List<QuestionResultDTO> getTeacherQuestion(@RequestParam("tid") Integer tid);

    @GetMapping("/homework-sql/question-get-type")
    List<QuestionResultDTO> getTypeQuestion(@RequestParam("type") String type);

    @GetMapping("/homework-sql/question-get-course")
    List<QuestionResultDTO> getCourseQuestion(@RequestParam("cid") Integer cid);

    @PostMapping("/homework-sql/question-create")
    HomeworkOpBO createNewQuestion(@RequestBody NewQuestionDTO newQuestion);

    @PostMapping("/homework-sql/question-update")
    HomeworkOpBO updateQuestion(@RequestBody NewQuestionDTO updateQuestion);

    @PostMapping("/homework-sql/question-delete")
    HomeworkOpBO deleteQuestion(@RequestBody TeacherQuestion tq);

    @PostMapping("/homework-sql/result-correct")
    HomeworkOpBO correctResult(@RequestBody NewResultDTO correctResult);

    @GetMapping("/homework-sql/question-types-get")
    List<String> getAType();

    @PostMapping("/homework-sql/question-type-add")
    Boolean createType(@RequestBody NewQuestionDTO questionType);

    @PostMapping("/homework-sql/question-type-delete")
    Boolean deleteType(@RequestBody NewQuestionDTO questionType);
}
