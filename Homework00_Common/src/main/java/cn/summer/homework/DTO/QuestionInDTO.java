package cn.summer.homework.DTO;

import cn.summer.homework.Entity.Question;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VHBin
 * @date 2022/8/8-15:15
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionInDTO {
    private Integer tid = 0; // create update delete create-type delete-type
    private Integer cid = 0; // create
    private Integer qid = 0; // update delete
    private Integer typeid = 0; // create-type delete-type
    private Question question = null; // create update
    private String type = ""; // create update create-type delete-type
}
