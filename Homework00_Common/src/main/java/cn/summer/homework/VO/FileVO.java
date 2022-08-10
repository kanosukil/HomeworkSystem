package cn.summer.homework.VO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VHBin
 * @date 2022/8/11-0:05
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FileVO {
    private Integer code;
    private String message;
    private String path;
}
