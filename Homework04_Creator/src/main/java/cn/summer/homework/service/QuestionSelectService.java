package cn.summer.homework.service;

import cn.summer.homework.DTO.QuestionResultDTO;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/15-15:17
 */
public interface QuestionSelectService {
    List<QuestionResultDTO> getAQuestion();

    QuestionResultDTO getQuestion(Integer qid);

    List<QuestionResultDTO> getTeacherQuestion(Integer tid);

    List<QuestionResultDTO> getTypeQuestion(String type);

    List<QuestionResultDTO> getCourseQuestion(Integer cid);

    List<String> getAType();
}
