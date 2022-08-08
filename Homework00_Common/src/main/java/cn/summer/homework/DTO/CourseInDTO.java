package cn.summer.homework.DTO;

import cn.summer.homework.Entity.Course;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VHBin
 * @date 2022/8/8-14:54
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseInDTO {
    private Integer tid = 0; // create update delete add drop
    private Integer cid = 0; // add-teacher drop-teacher delete
    private Integer sid = 0; // add-student drop-student
    private Course course = null; // create update
}
