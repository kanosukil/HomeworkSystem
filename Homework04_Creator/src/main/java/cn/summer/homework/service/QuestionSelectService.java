package cn.summer.homework.service;

import cn.summer.homework.DTO.QuestionResultDTO;

import java.io.IOException;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/15-15:17
 */
public interface QuestionSelectService {
    List<QuestionResultDTO> getAQuestion();

    QuestionResultDTO getQuestion(Integer qid) throws IOException;

    List<QuestionResultDTO> getTeacherQuestion(Integer tid) throws IOException;

    List<QuestionResultDTO> getTypeQuestion(String type) throws IOException;

    List<QuestionResultDTO> getCourseQuestion(Integer cid) throws IOException;

    List<String> getAType();
}
