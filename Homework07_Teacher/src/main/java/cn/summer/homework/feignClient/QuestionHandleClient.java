package cn.summer.homework.feignClient;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.NewQuestionDTO;
import cn.summer.homework.DTO.NewResultDTO;
import cn.summer.homework.PO.TeacherQuestion;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.io.IOException;

/**
 * @author VHBin
 * @date 2022/8/7-15:19
 */

@FeignClient("CreatorService")
public interface QuestionHandleClient {
    @PostMapping("/create/new")
    HomeworkOpBO createNew(@RequestBody NewQuestionDTO newQuestion);

    @PostMapping("/create/update")
    HomeworkOpBO update(@RequestBody NewQuestionDTO updateQuestion);

    @PostMapping("/create/delete")
    HomeworkOpBO delete(@RequestBody TeacherQuestion tq);

    @PostMapping("/create/correct")
    HomeworkOpBO correct(@RequestBody NewResultDTO correctResult);

    @PostMapping("/create/new/type")
    Boolean createT(@RequestBody NewQuestionDTO qt)
            throws IOException;

    @PostMapping("/create/delete/type")
    Boolean deleteT(@RequestBody NewQuestionDTO qt)
            throws IOException;
}
