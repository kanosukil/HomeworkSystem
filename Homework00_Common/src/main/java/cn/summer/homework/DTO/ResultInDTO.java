package cn.summer.homework.DTO;

import cn.summer.homework.Entity.Result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author VHBin
 * @date 2022/8/8-15:18
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultInDTO {
    private Integer rid = 0; // delete
    private Integer qid = 0; // correct create update
    private Integer cid = 0; // correct create update
    private Integer sid = 0; // create update delete
    private Integer tid = 0; // correct
    private Result result = null; // correct create update
}
