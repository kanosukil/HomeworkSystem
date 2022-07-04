package cn.summer.homework.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VHBin
 * @date 2022/7/4-23:41
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CourseInfo {
    private String courseName;
    private Integer number;
    private Object info;
}
