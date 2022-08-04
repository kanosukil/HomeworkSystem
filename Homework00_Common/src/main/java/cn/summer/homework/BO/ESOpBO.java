package cn.summer.homework.BO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author VHBin
 * @date 2022/8/4-18:04
 */

@Data
@NoArgsConstructor
public class ESOpBO {
    private Boolean isSuccess = false;
    private Map<String, Object> map;
}
