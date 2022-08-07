package cn.summer.homework.feignClient;

import cn.summer.homework.DTO.QuestionResultDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/7-15:10
 */

@FeignClient("CreatorService")
public interface QuestionFindClient {
    @GetMapping("/query/question/all")
    List<QuestionResultDTO> getAll();

    @GetMapping("/query/question/id")
    QuestionResultDTO get(@RequestParam("id") Integer qid)
            throws IOException;

    @GetMapping("/query/question/teacher")
    List<QuestionResultDTO> getTeacherQuestion(@RequestParam("tid") Integer tid)
            throws IOException;

    @GetMapping("/query/question/type")
    List<QuestionResultDTO> getTypeQuestion(@RequestParam("type") String type)
            throws IOException;

    @GetMapping("/query/question/course")
    List<QuestionResultDTO> getCourseQuestion(@RequestParam("cid") Integer cid)
            throws IOException;

    @GetMapping("/query/question/all-type")
    List<String> getAType();
}
