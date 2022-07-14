package cn.summer.homework.controller;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.NewResultDTO;
import cn.summer.homework.DTO.ResultQuestionDTO;
import cn.summer.homework.feignClient.ResultSQLClient;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/15-0:04
 */

@RestController
@RefreshScope
@RequestMapping("result")
public class ResultServiceController {
    @Resource
    private ResultSQLClient sqlClient;

    @GetMapping("results-get")
    public List<ResultQuestionDTO> getAResult() {
        return sqlClient.getAResult();
    }

    @GetMapping("result-get")
    public ResultQuestionDTO getResult(@RequestParam("rid") Integer rid) {
        return sqlClient.getResult(rid);
    }

    @GetMapping("result-get-course")
    public List<ResultQuestionDTO> getCourseResult(@RequestParam("cid") Integer cid) {
        return sqlClient.getCourseResult(cid);
    }

    @PostMapping("result-create")
    public HomeworkOpBO insertResult(@RequestBody NewResultDTO newResult) {
        return sqlClient.insertResult(newResult);
    }

    @PostMapping("result-update")
    public HomeworkOpBO updateResult(@RequestBody NewResultDTO updateResult) {
        return sqlClient.updateResult(updateResult);
    }

    @PostMapping("result-delete")
    public HomeworkOpBO deleteResult(@RequestBody NewResultDTO deleteResult) {
        return sqlClient.deleteResult(deleteResult);
    }
}
