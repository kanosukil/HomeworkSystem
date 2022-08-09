package cn.summer.homework.VO;

import cn.summer.homework.DTO.QuestionResultDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/9-15:30
 */

@AllArgsConstructor
@NoArgsConstructor
@Data
public class SearchQuestionVO {
    private Integer code;
    private String message;
    private List<QuestionResultDTO> questions;
}
