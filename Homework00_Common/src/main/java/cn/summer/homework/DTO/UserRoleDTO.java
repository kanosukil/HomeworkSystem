package cn.summer.homework.DTO;

import cn.summer.homework.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/6-14:30
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRoleDTO implements Serializable {
    private User user = null;
    private List<String> roles = null;
}
