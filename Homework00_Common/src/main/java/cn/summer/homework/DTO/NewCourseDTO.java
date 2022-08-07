package cn.summer.homework.DTO;

import cn.summer.homework.Entity.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VHBin
 * @date 2022/7/13-17:19
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewCourseDTO {
    private Course course;
    private Integer tid = 0;
}
