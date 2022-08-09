package cn.summer.homework.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/4-17:08
 */

@Data
@NoArgsConstructor
public class ElasticSearchDTO {
    /*
        Option:
        1. 创建目录
            option=1, index=str
        2. 创建文档
            option=2, index=str, size=id, objects=list(size=1)
        3. 批量创建文档
            option=3, index=str, objects=list(size>1)
        4. 删除目录
            option=4, index=str
        5. 删除文档
            option=5, index=str, size=id
        6. 更新文档
            option=6, index=str, size=id, objects=list(size=1)
        7. 查询
            option=7, index=str, value=str[, from=from, size=size]
     */
    private Integer option;
    private String index;
    private String value = null;
    /*
    Object 只能是:
        1. CourseSTDTO
        2. QuestionResultDTO
        3. ResultQuestionDTO
        4. UserRoleDTO
     */
    private List<Object> objects = null;
    private Integer from = null;
    private Integer size = null;
}
