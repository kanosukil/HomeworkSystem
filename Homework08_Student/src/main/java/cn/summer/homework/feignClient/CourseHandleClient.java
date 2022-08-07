package cn.summer.homework.feignClient;

import cn.summer.homework.BO.CourseOpBO;
import cn.summer.homework.PO.StudentCourse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author VHBin
 * @date 2022/8/7-15:23
 */

@FeignClient("CourseManagerService")
public interface CourseHandleClient {
    @PostMapping("/course-handle/student/drop")
    CourseOpBO dropS(@RequestBody StudentCourse sc);

    @PostMapping("/course-handle/student/add")
    CourseOpBO addS(@RequestBody StudentCourse sc);
}
