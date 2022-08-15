package cn.summer.homework.service.impl;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.NewResultDTO;
import cn.summer.homework.Entity.Result;
import cn.summer.homework.Util.OpBOUtil;
import cn.summer.homework.feignClient.ResultClient;
import cn.summer.homework.service.ResultIOService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

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
            return OpBOUtil.generateHOBr("新建回答传入数据不能为空");
        }
        if (result.getId() == 0 || result.getIsFile() == null
                || result.getContent() == null || result.getCreate_time() == null) {
            return OpBOUtil.generateHOBr("新建回答的数据不能空");
        }
        result.setCreate_time(new Date());
        result.setIsCheck(false);
        result.setScore(0);
        result.setComment("");
        newResult.setResult(result);
        return client.insertResult(newResult);
    }

    @Override
    public HomeworkOpBO updateResult(NewResultDTO updateResult) {
        Result result = updateResult.getResult();
        if (result == null || updateResult.getUid() == 0) {
            return OpBOUtil.generateHOBr("更新回答传入数据不能为空");
        }
        if (result.getId() == 0 || result.getIsFile() == null
                || result.getContent() == null) {
            return OpBOUtil.generateHOBr("更新回答的传入数据不能为空");
        }
        Boolean isCheck = result.getIsCheck();
        if (isCheck == null) {
            result.setIsCheck(false);
            isCheck = false;
        }
        if (isCheck) {
            return OpBOUtil.generateHOBr("回答已批改, 不能再更新");
        }
        Result before = client.getResult(result.getId()).getResult();
        result.setCreate_time(before.getCreate_time());
        if (result.getScore() == null || result.getScore() < before.getScore()) {
            result.setScore(before.getScore());
        }
        updateResult.setResult(result);
        return client.updateResult(updateResult);
    }

    @Override
    public HomeworkOpBO deleteResult(NewResultDTO deleteResult) {
        if (deleteResult.getUid() == 0 || deleteResult.getQid() == 0) {
            return OpBOUtil.generateHOBr("删除回答的传入数据不能为空");
        }
        return client.deleteResult(deleteResult);
    }
}
