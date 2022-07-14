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
    private Integer uid; // Student: sid; TeacherCorrect: tid
    private Integer cid;
    private Integer qid; // insert/update: questionID; delete: resultID
    private Result result;
}
