package cn.summer.homework.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VHBin
 * @date 2022/8/5-15:43
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private Integer id = 0;
    private String account = "";
}
