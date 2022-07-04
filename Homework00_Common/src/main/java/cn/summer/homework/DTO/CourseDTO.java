package cn.summer.homework.DTO;

import cn.summer.homework.Entity.Course;
import cn.summer.homework.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VHBin
 * @date 2022/7/4-23:43
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseDTO {
    private Course course;
    private User[] teacher;
    private User[] students;
}
