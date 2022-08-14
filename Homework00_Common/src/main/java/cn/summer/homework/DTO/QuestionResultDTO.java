package cn.summer.homework.DTO;

import cn.summer.homework.Entity.Question;
import cn.summer.homework.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author VHBin
 * @date 2022/7/7-13:34
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionResultDTO implements Serializable {
    private Question question;
    private String questionType;
    private User teacher;
    private Map<Integer, Integer> results; // key: uid, value: rid
}
