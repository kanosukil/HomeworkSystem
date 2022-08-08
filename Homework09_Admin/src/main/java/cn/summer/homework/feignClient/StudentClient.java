package cn.summer.homework.feignClient;

import cn.summer.homework.DTO.CourseInDTO;
import cn.summer.homework.DTO.ResultInDTO;
import cn.summer.homework.VO.StudentVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author VHBin
 * @date 2022/8/8-17:05
 */

@FeignClient("StudentService")
public interface StudentClient {
    @PostMapping("/s/c/result")
    StudentVO createResult(@RequestBody ResultInDTO in);

    @PostMapping("/s/u/result")
    StudentVO updateResult(@RequestBody ResultInDTO in);

    @PostMapping("/s/d/result")
    StudentVO deleteResult(@RequestBody ResultInDTO in);

    @PostMapping("/s/ao/add/course")
    StudentVO addCourse(@RequestBody CourseInDTO in);

    @PostMapping("/s/ao/drop/course")
    StudentVO dropCourse(@RequestBody CourseInDTO in);
}
