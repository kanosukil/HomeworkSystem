package cn.summer.homework.VO;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VHBin
 * @date 2022/8/5-18:05
 */

@Data
@NoArgsConstructor
public class UserVO<T> {
    private Integer code;
    private String msg;
    private Boolean isAdmin = false;
    private T object;

    public UserVO(Integer code, String msg, T object) {
        this.code = code;
        this.msg = msg;
        this.object = object;
    }

    public UserVO(Integer code, String msg, Boolean isAdmin, T object) {
        this.code = code;
        this.msg = msg;
        this.isAdmin = isAdmin;
        this.object = object;
    }
}
