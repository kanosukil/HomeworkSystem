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
 * @date 2022/7/15-15:05
 */

@FeignClient("HomeworkSQL")
public interface ResultClient {
    @GetMapping("/result/results-get")
    List<ResultQuestionDTO> getAResult();

    @GetMapping("/result/result-get")
    ResultQuestionDTO getResult(@RequestParam("rid") Integer rid);

    @GetMapping("/result/result-get-course")
    List<ResultQuestionDTO> getCourseResult(@RequestParam("cid") Integer cid);

    @GetMapping("/result/result-get-student")
    List<ResultQuestionDTO> getStudentResult(@RequestParam("sid") Integer sid);

    @GetMapping("/result/result-get-question")
    List<ResultQuestionDTO> getQuestionResult(@RequestParam("qid") Integer qid);

    @PostMapping("/result/result-create")
    HomeworkOpBO insertResult(@RequestBody NewResultDTO newResult);

    @PostMapping("/result/result-update")
    HomeworkOpBO updateResult(@RequestBody NewResultDTO updateResult);

    @PostMapping("/result/result-delete")
    HomeworkOpBO deleteResult(@RequestBody NewResultDTO deleteResult);
}
