package cn.summer.homework.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/11-15:29
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class URoleDTO {
    private Integer uid = 0;
    private List<String> roles = null;
}
