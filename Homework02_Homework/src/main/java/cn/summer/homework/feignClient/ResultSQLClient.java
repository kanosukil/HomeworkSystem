package cn.summer.homework.feignClient;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.NewResultDTO;
import cn.summer.homework.DTO.ResultQuestionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/14-23:40
 */

@FeignClient("SQLService")
public interface ResultSQLClient {
    @GetMapping("/homework/results-get")
    List<ResultQuestionDTO> getAResult();

    @GetMapping("/homework/result-get")
    ResultQuestionDTO getResult(@RequestParam("rid") Integer rid);

    @GetMapping("/homework/result-get-course")
    List<ResultQuestionDTO> getCourseResult(@RequestParam("cid") Integer cid);

    @PostMapping("/homework/result-create")
    HomeworkOpBO insertResult(@RequestBody NewResultDTO newResult);

    @PostMapping("/homework/result-update")
    HomeworkOpBO updateResult(@RequestBody NewResultDTO updateResult);

    @PostMapping("/homework/result-delete")
    HomeworkOpBO deleteResult(@RequestBody NewResultDTO deleteResult);
}
