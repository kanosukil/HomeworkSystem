package cn.summer.homework.DTO;

import cn.summer.homework.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VHBin
 * @date 2022/7/4-16:34
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UserDTO {
    private User user;
    private String[] roles;
}
