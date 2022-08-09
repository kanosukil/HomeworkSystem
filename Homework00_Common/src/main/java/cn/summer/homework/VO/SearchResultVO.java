package cn.summer.homework.VO;

import cn.summer.homework.DTO.ResultQuestionDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/9-15:41
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchResultVO {
    private Integer code;
    private String message;
    private List<ResultQuestionDTO> results;
}
