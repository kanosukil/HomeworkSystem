package cn.summer.homework.feignClient;

import cn.summer.homework.DTO.CourseInDTO;
import cn.summer.homework.DTO.QuestionInDTO;
import cn.summer.homework.DTO.ResultInDTO;
import cn.summer.homework.VO.TeacherVO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author VHBin
 * @date 2022/8/8-17:04
 */

@FeignClient("TeacherService")
public interface TeacherClient {
    @PostMapping("/t/ao/correct/question")
    TeacherVO correct(@RequestBody ResultInDTO in);

    @PostMapping("/t/ao/add/course")
    TeacherVO addCourse(@RequestBody CourseInDTO in);

    @PostMapping("/t/ao/drop/course")
    TeacherVO dropCourse(@RequestBody CourseInDTO in);

    @PostMapping("/t/c/course")
    TeacherVO createCourse(@RequestBody CourseInDTO in);

    @PostMapping("/t/u/course")
    TeacherVO updateCourse(@RequestBody CourseInDTO in);

    @PostMapping("/t/d/course")
    TeacherVO deleteCourse(@RequestBody CourseInDTO in);

    @PostMapping("/t/c/question")
    TeacherVO createQuestion(@RequestBody QuestionInDTO in);

    @PostMapping("/t/u/question")
    TeacherVO updateQuestion(@RequestBody QuestionInDTO in);

    @PostMapping("/t/d/question")
    TeacherVO deleteQuestion(@RequestBody QuestionInDTO in);

    @PostMapping("/t/c/type")
    TeacherVO createType(@RequestBody QuestionInDTO in);

    @PostMapping("/t/d/type")
    TeacherVO deleteType(@RequestBody QuestionInDTO in);
}
