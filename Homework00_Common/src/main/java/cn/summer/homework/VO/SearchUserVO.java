package cn.summer.homework.VO;

import cn.summer.homework.DTO.UserRoleDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/9-14:44
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchUserVO {
    private Integer code;
    private String message;
    private List<UserRoleDTO> users;
}
