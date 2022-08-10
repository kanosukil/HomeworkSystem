package cn.summer.homework.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/10-23:57
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchVO<T> {
    private Integer code;
    private String message;
    private List<T> courses;
}
