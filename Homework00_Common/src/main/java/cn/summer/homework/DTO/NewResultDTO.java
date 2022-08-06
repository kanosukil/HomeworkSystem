package cn.summer.homework.DTO;

import cn.summer.homework.Entity.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VHBin
 * @date 2022/7/13-23:37
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewResultDTO {
    private Integer uid = 0; // Student: sid; TeacherCorrect: tid
    private Integer cid = 0;
    private Integer qid = 0; // insert/update: questionID; delete: resultID
    private Result result = null;
}
