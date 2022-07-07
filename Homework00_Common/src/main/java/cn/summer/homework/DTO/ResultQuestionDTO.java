package cn.summer.homework.DTO;

import cn.summer.homework.Entity.Question;
import cn.summer.homework.Entity.Result;
import cn.summer.homework.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @author VHBin
 * @date 2022/7/7-14:10
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResultQuestionDTO {
    private Result result;
    private User student;
    private Map<User, Map<String, Question>> question;
}
