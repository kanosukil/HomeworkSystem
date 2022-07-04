package cn.summer.homework.DTO;

import lombok.Data;

/**
 * @author VHBin
 * @date 2022/7/4-15:20
 */

@Data
public class UserInfo {
    private String username; // 查找的用户名
    // 使用 id 查找: username="Op:selectByID"
    // 使用 email 查找: username="Op:selectByEmail"
    // 插入: username="Op:insert"
    // 删除: username="Op:delete"
    // 更新: username="Op:update"
    private Integer number; // 查找到的结果数量
    private Object info; // 信息
}
