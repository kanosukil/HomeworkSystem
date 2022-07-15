package cn.summer.homework.service;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.NewResultDTO;

/**
 * @author VHBin
 * @date 2022/7/15-18:24
 */

public interface ResultIOService {
    HomeworkOpBO insertResult(NewResultDTO newResult);

    HomeworkOpBO updateResult(NewResultDTO updateResult);

    HomeworkOpBO deleteResult(NewResultDTO deleteResult);
}
