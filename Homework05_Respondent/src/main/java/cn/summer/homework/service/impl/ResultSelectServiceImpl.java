package cn.summer.homework.service.impl;

import cn.summer.homework.DTO.ResultQuestionDTO;
import cn.summer.homework.feignClient.ResultClient;
import cn.summer.homework.service.ResultSelectService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/6-23:56
 */

@Service
public class ResultSelectServiceImpl implements ResultSelectService {
    @Resource
    private ResultClient client;

    @Override
    public List<ResultQuestionDTO> getAResult() {
        return null;
    }

    @Override
    public ResultQuestionDTO getResult(Integer rid) {
        return null;
    }

    @Override
    public List<ResultQuestionDTO> getCourseResult(Integer cid) {
        return null;
    }
}
