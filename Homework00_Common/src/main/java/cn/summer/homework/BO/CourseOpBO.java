package cn.summer.homework.BO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author VHBin
 * @date 2022/7/6-15:47
 */

@Data
@NoArgsConstructor
public class CourseOpBO {
    private Boolean isSuccess;
    private Map<String, Object> map;
}
