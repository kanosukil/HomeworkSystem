package cn.summer.homework.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VHBin
 * @date 2022/8/5-18:02
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginDTO {
    private String account;
    private String password;
}
