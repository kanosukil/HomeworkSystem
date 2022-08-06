package cn.summer.homework.service;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.NewQuestionDTO;
import cn.summer.homework.DTO.NewResultDTO;
import cn.summer.homework.PO.TeacherQuestion;

import java.io.IOException;

/**
 * @author VHBin
 * @date 2022/7/15-15:16
 */

public interface QuestionIOService {
    HomeworkOpBO createNewQuestion(NewQuestionDTO newQuestion);

    HomeworkOpBO updateQuestion(NewQuestionDTO updateQuestion);

    HomeworkOpBO deleteQuestion(TeacherQuestion tq);

    HomeworkOpBO correctResult(NewResultDTO correctResult);

    Boolean createType(NewQuestionDTO questionType) throws IOException;

    Boolean deleteType(NewQuestionDTO questionType) throws IOException;
}
