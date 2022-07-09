package cn.summer.homework.PO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author VHBin
 * @date 2022/7/3-15:41
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionType implements Serializable {
    private Integer qid;
    private Integer qtid;

}
