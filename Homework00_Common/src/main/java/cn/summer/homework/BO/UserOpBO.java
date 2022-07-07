package cn.summer.homework.BO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author VHBin
 * @date 2022/7/6-13:40
 */

@Data
@NoArgsConstructor
public class UserOpBO {
    private Boolean isSuccess;
    private Map<String, Object> info;
}
