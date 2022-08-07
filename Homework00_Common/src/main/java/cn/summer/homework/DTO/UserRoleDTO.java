package cn.summer.homework.DTO;

import cn.summer.homework.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/6-14:30
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleDTO {
    private User user = null;
    private List<String> roles = null;
}
