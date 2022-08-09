package cn.summer.homework.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VHBin
 * @date 2022/8/9-14:52
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchDTO {
    private String value;
    private Integer from = 0;
    private Integer size = 0;
}
