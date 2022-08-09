package cn.summer.homework.VO;

import cn.summer.homework.DTO.CourseSTDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/9-15:13
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchCourseVO {
    private Integer code;
    private String message;
    private List<CourseSTDTO> courses;
}
