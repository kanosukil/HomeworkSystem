package cn.summer.homework.BO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author VHBin
 * @date 2022/7/7-13:39
 */

@Data
@NoArgsConstructor
public class HomeworkOpBO {
    private Boolean isSuccess;
    private Boolean isQuestion;
    private Map<String, Object> info;
}
