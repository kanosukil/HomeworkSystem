package cn.summer.homework.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VHBin
 * @date 2022/8/8-14:37
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StudentVO {
    private Integer code;
    private String msg;
    private String info;
}
