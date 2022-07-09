package cn.summer.homework.PO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author VHBin
 * @date 2022/7/3-15:45
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TeacherCourse implements Serializable {
    private Integer tid;
    private Integer cid;
}
