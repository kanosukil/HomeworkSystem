package cn.summer.homework.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VHBin
 * @date 2022/8/5-18:05
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserVO<T> {
    private Integer code;
    private String msg;
    private Boolean isAdmin = false;
    private Boolean isTeacher = false;
    private Boolean isStudent = false;
    private Integer uid;
    private T object;

    public UserVO(Integer code, String msg, T object) {
        this.code = code;
        this.msg = msg;
        this.object = object;
    }

}
