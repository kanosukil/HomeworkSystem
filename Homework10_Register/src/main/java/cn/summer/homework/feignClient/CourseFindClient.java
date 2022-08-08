package cn.summer.homework.feignClient;

import cn.summer.homework.DTO.CourseSTDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/7-15:15
 */

@FeignClient("CourseManagerService")
public interface CourseFindClient {
    @GetMapping("/query/course/all")
    List<CourseSTDTO> getAll();

    @GetMapping("/query/course/id")
    CourseSTDTO get(@RequestParam("id") Integer id)
            throws IOException;

    @GetMapping("/query/course/name")
    List<CourseSTDTO> getn(@RequestParam("name") String name)
            throws IOException;

    @GetMapping("/query/course/teacher")
    List<CourseSTDTO> gett(@RequestParam("tid") Integer tid)
            throws IOException;

    @GetMapping("/query/course/student")
    List<CourseSTDTO> gets(@RequestParam("sid") Integer sid)
            throws IOException;
}
