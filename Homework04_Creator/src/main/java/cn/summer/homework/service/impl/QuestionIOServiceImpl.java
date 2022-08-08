package cn.summer.homework.service.impl;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.NewQuestionDTO;
import cn.summer.homework.DTO.NewResultDTO;
import cn.summer.homework.Entity.Question;
import cn.summer.homework.Entity.Result;
import cn.summer.homework.PO.TeacherQuestion;
import cn.summer.homework.Util.OpBOUtil;
import cn.summer.homework.feignClient.QuestionClient;
import cn.summer.homework.service.QuestionIOService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Date;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/8/6-22:23
 */

@Service
public class QuestionIOServiceImpl implements QuestionIOService {
    @Resource
    private QuestionClient client;

    @Override
    public HomeworkOpBO createNewQuestion(NewQuestionDTO newQuestion) {
        Question question = newQuestion.getQuestion();
        if (question == null || newQuestion.getType().equals("") ||
                newQuestion.getId() == 0 || newQuestion.getTid() == 0) {
            return OpBOUtil.generateHOBq("新创建问题的传入数据不能为空");
        }
        if (question.getTitle() == null || question.getScore() == null) {
            return OpBOUtil.generateHOBq("新创建问题的数据不能为空");
        }
        question.setCreate_time(new Date());
        newQuestion.setQuestion(question);
        return client.createNewQuestion(newQuestion);
    }

    @Override
    public HomeworkOpBO updateQuestion(NewQuestionDTO updateQuestion) {
        Question question = updateQuestion.getQuestion();
        if (question == null || updateQuestion.getType().equals("") ||
                updateQuestion.getId() == 0 || updateQuestion.getTid() == 0) {
            return OpBOUtil.generateHOBq("更新问题的传入数据不能为空");
        }
        if (question.getTitle() == null || question.getId() == 0 ||
                question.getIsFile() == null || question.getScore() == null ||
                question.getAnswer() == null || question.getComment() == null ||
                question.getExtension() == null) {
            return OpBOUtil.generateHOBq("更新问题的数据不能为空");
        }
        question.setCreate_time(client.getQuestion(question.getId())
                .getQuestion().getCreate_time());
        updateQuestion.setQuestion(question);
        return client.updateQuestion(updateQuestion);
    }

    @Override
    public HomeworkOpBO deleteQuestion(TeacherQuestion tq) {
        if (tq == null || tq.getTid() == null || tq.getQid() == null) {
            return OpBOUtil.generateHOBq("删除问题的传入数据不能为空");
        }
        return client.deleteQuestion(tq);
    }

    @Override
    public HomeworkOpBO correctResult(NewResultDTO correctResult) {
        Result result = correctResult.getResult();
        if (result == null || correctResult.getQid() == 0 ||
                correctResult.getCid() == 0 || correctResult.getUid() == 0) {
            return OpBOUtil.generateHOBq("批改时传入的数据不能为空");
        }
        if (result.getId() == 0 || result.getIsCheck() == null ||
                result.getIsFile() == null || result.getComment() == null ||
                result.getScore() == null || result.getContent() == null) {
            return OpBOUtil.generateHOBq("批改的答案数据不能为空");
        }
        if (!result.getIsCheck()) {
            return OpBOUtil.generateHOBq("批改标识未修改");
        }
        if (result.getScore() < 0
                || result.getScore() > client.getQuestion(correctResult.getQid())
                .getQuestion().getScore()) {
            return OpBOUtil.generateHOBq("批改分数不符规定");
        }
        return client.correctResult(correctResult);
    }

    @Override
    public Boolean createType(NewQuestionDTO questionType)
            throws IOException {
        String ty = questionType.getType();
        if (questionType.getTid() == 0 || ty.equals("")) {
            throw new IOException("添加问题类型传入数据无效");
        }
        List<String> type = client.getAType();
        for (String s : type) {
            if (s.equals(ty)) {
                throw new IOException("添加问题类型传入类型已存在");
            }
        }
        return client.createType(questionType);
    }

    @Override
    public Boolean deleteType(NewQuestionDTO questionType)
            throws IOException {
        if (questionType.getTid() == 0 ||
                (questionType.getId() == 0 && questionType.getType().equals(""))) {
            throw new IOException("删除问题类型传入数据无效");
        }
        return client.deleteType(questionType);
    }
}
