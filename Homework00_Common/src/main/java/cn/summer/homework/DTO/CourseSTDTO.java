package cn.summer.homework.DTO;

import cn.summer.homework.Entity.Course;
import cn.summer.homework.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/6-15:48
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseSTDTO {
    private Course course;
    private List<User> teachers;
    private List<User> students;
}
