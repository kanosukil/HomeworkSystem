package cn.summer.homework.feignClient;

import cn.summer.homework.DTO.ResultQuestionDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/7-15:14
 */

@FeignClient("RespondentService")
public interface ResultFindClient {
    @GetMapping("/query/result/all")
    List<ResultQuestionDTO> getAll();

    @GetMapping("/query/result/id")
    ResultQuestionDTO get(@RequestParam("id") Integer rid)
            throws IOException;

    @GetMapping("/query/result/course")
    List<ResultQuestionDTO> getCourseResult(@RequestParam("cid") Integer cid)
            throws IOException;
}
