package cn.summer.homework.service.impl;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.NewResultDTO;
import cn.summer.homework.Entity.Result;
import cn.summer.homework.Util.OpBOUtil;
import cn.summer.homework.feignClient.ResultClient;
import cn.summer.homework.service.ResultIOService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author VHBin
 * @date 2022/8/6-23:55
 */

@Service
public class ResultIOServiceImpl implements ResultIOService {
    @Resource
    private ResultClient client;

    @Override
    public HomeworkOpBO insertResult(NewResultDTO newResult) {
        Result result = newResult.getResult();
        if (result == null || newResult.getUid() == 0 ||
                newResult.getCid() == 0 || newResult.getQid() == 0) {
            return OpBOUtil.generateHOBr("");
        }
        return null;
    }

    @Override
    public HomeworkOpBO updateResult(NewResultDTO updateResult) {
        return null;
    }

    @Override
    public HomeworkOpBO deleteResult(NewResultDTO deleteResult) {
        return null;
    }
}
