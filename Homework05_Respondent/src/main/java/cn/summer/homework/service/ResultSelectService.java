package cn.summer.homework.service;

import cn.summer.homework.DTO.ResultQuestionDTO;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/15-18:24
 */
public interface ResultSelectService {
    List<ResultQuestionDTO> getAResult();

    ResultQuestionDTO getResult(Integer rid);

    List<ResultQuestionDTO> getCourseResult(Integer cid);
}
