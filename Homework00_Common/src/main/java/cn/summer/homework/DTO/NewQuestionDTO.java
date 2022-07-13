package cn.summer.homework.DTO;

import cn.summer.homework.Entity.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VHBin
 * @date 2022/7/13-19:03
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewQuestionDTO {
    private Integer tid = 0;
    private Integer id = 0; // create: cid; update: qid
    // type: create 0; delete 0/typeID
    private Question question = null;
    private String type = "";
}
