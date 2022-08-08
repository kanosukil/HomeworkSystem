package cn.summer.homework.feignClient;

import cn.summer.homework.DTO.CourseInDTO;
import cn.summer.homework.DTO.QuestionInDTO;
import cn.summer.homework.DTO.ResultInDTO;
import cn.summer.homework.VO.AdminVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author VHBin
 * @date 2022/8/8-18:00
 */

@FeignClient("AdminService")
public interface AdminClient {
    @PostMapping("/a/c/course")
    AdminVO createCourse(@RequestBody CourseInDTO in);

    @PostMapping("/a/c/question")
    AdminVO createQuestion(@RequestBody QuestionInDTO in);

    @PostMapping("/a/c/type")
    AdminVO createType(@RequestBody QuestionInDTO in);

    @PostMapping("/a/c/result")
    AdminVO createResult(@RequestBody ResultInDTO in);

    @PostMapping("/a/u/course")
    AdminVO updateCourse(@RequestBody CourseInDTO in);

    @PostMapping("/a/u/question")
    AdminVO updateQuestion(@RequestBody QuestionInDTO in);

    @PostMapping("/a/u/result")
    AdminVO updateResult(@RequestBody ResultInDTO in);

    @PostMapping("/a/d/course")
    AdminVO deleteCourse(@RequestBody CourseInDTO in);

    @PostMapping("/a/d/question")
    AdminVO deleteQuestion(@RequestBody QuestionInDTO in);

    @PostMapping("/a/d/type")
    AdminVO deleteType(@RequestBody QuestionInDTO in);

    @PostMapping("/a/d/result")
    AdminVO deleteResult(@RequestBody ResultInDTO in);
}
