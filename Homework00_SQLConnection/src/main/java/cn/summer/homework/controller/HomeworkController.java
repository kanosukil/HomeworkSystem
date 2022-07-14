package cn.summer.homework.controller;

import cn.summer.homework.BO.HomeworkOpBO;
import cn.summer.homework.DTO.NewQuestionDTO;
import cn.summer.homework.DTO.NewResultDTO;
import cn.summer.homework.DTO.QuestionResultDTO;
import cn.summer.homework.DTO.ResultQuestionDTO;
import cn.summer.homework.Entity.Question;
import cn.summer.homework.PO.TeacherQuestion;
import cn.summer.homework.service.HomeworkService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;

/**
 * @author VHBin
 * @date 2022/7/13-18:37
 */

@RefreshScope
@RestController
@RequestMapping("homework-sql")
public class HomeworkController {
    private static final Logger logger = LoggerFactory.getLogger(HomeworkController.class);
    @Resource
    private HomeworkService homeworkService;

    /*
        老师
     */
    // 查询
    @GetMapping("questions-get")
    public List<QuestionResultDTO> getAQuestion() {
        return homeworkService.selectAllHK_T();
    }

    @GetMapping("question-get")
    public QuestionResultDTO getQuestion(@RequestParam("qid") Integer qid) {
        try {
            return homeworkService.selectHKByQID(qid);
        } catch (Exception ex) {
            logger.error("指定问题查询异常: {}", ex.getMessage());
            return null;
        }
    }

    @GetMapping("questions-get-tid")
    public List<QuestionResultDTO> getTeacherQuestion(@RequestParam("tid") Integer tid) {
        try {
            return homeworkService.selectHKByTID(tid);
        } catch (Exception ex) {
            logger.error("查询指定教师教授的问题异常: {}", ex.getMessage());
            return null;
        }
    }

    @GetMapping("question-get-type")
    public List<QuestionResultDTO> getTypeQuestion(@RequestParam("type") String type) {
        try {
            return homeworkService.selectHKByQuestionType(type);
        } catch (Exception ex) {
            logger.error("查询指定类型问题异常: {}", ex.getMessage());
            return null;
        }
    }

    @GetMapping("question-get-course")
    public List<QuestionResultDTO> getCourseQuestion(@RequestParam("cid") Integer cid) {
        try {
            return homeworkService.selectHKByCID_T(cid);
        } catch (Exception ex) {
            logger.error("查询指定课程的问题异常: {}", ex.getMessage());
            return null;
        }
    }

    private void setRes_Q(HomeworkOpBO res, Boolean isSuccess, String key, Object value) {
        res.setIsSuccess(isSuccess);
        res.setIsQuestion(true);
        res.setInfo(new HashMap<>(1, 1f) {{
            put(key, value);
        }});
    }

    // 插入
    @PostMapping("question-create")
    public HomeworkOpBO createNewQuestion(@RequestBody NewQuestionDTO newQuestion) {
        HomeworkOpBO res = new HomeworkOpBO();
        try {
            HomeworkOpBO question = homeworkService.createQuestion(
                    newQuestion.getTid(),
                    newQuestion.getId(),
                    newQuestion.getQuestion(),
                    newQuestion.getType());
            if (!question.getIsSuccess()) {
                throw new Exception("新建问题失败");
            }
            logger.info("问题新建完成");
            setRes_Q(res, true, "qid",
                    ((QuestionResultDTO) question.getInfo().get("新建问题")).getQuestion().getId());
        } catch (Exception ex) {
            logger.error("问题新建异常: {}", ex.getMessage());
            setRes_Q(res, false, "Cause", ex.getCause());
        }
        return res;
    }

    // 更新
    @PostMapping("question-update")
    public HomeworkOpBO updateQuestion(@RequestBody NewQuestionDTO updateQuestion) {
        HomeworkOpBO res = new HomeworkOpBO();
        try {
            Integer tid = updateQuestion.getTid();
            Question question = updateQuestion.getQuestion();
            Integer qid = updateQuestion.getId();
            String type = updateQuestion.getType();
            HomeworkOpBO op;
            if (type.equals("")) {
                op = homeworkService.updateQuestion(tid, question);
            } else if (question == null) {
                op = homeworkService.updateQuestion(tid, qid, type);
            } else {
                op = homeworkService.updateQuestion(tid, question, type);
            }
            if (!op.getIsSuccess()) {
                throw new Exception("问题更新失败");
            }
            logger.info("更新问题完成");
            setRes_Q(res, true, "Question",
                    op.getInfo().get("updateQuestion"));
        } catch (Exception ex) {
            logger.error("更新问题异常: {}", ex.getMessage());
            setRes_Q(res, false, "Cause", ex.getCause());
        }
        return res;
    }

    // 删除
    @PostMapping("question-delete")
    public HomeworkOpBO deleteQuestion(@RequestBody TeacherQuestion tq) {
        HomeworkOpBO res = new HomeworkOpBO();
        try {
            HomeworkOpBO question
                    = homeworkService.deleteQuestion(tq.getTid(), tq.getQid());
            if (!question.getIsSuccess()) {
                throw new Exception("删除问题失败");
            }
            logger.info("问题删除完成");
            setRes_Q(res, true, "Question",
                    question.getInfo().get("Question".concat(tq.getQid().toString())));
        } catch (Exception ex) {
            logger.error("删除问题异常: {}", ex.getMessage());
            setRes_Q(res, false, "Cause", ex.getCause());
        }
        return res;
    }

    // 老师批改
    @PostMapping("result-correct")
    public HomeworkOpBO correctResult(@RequestBody NewResultDTO correctResult) {
        HomeworkOpBO res = new HomeworkOpBO();
        try {
            HomeworkOpBO resultCorrect = homeworkService.correctResult(
                    correctResult.getUid(),
                    correctResult.getQid(),
                    correctResult.getResult());
            if (!resultCorrect.getIsSuccess()) {
                throw new Exception("教师批改作业失败");
            }
            logger.info("批改作业完成");
            setRes_R(res, true, "Result",
                    resultCorrect.getInfo().get("updateResult"));
        } catch (Exception ex) {
            logger.error("教师批改异常: {}", ex.getMessage());
            setRes_R(res, false, "Cause", ex.getCause());
        }
        return res;
    }

    // 类型操作
    @GetMapping("question-types-get")
    public List<String> getAType() {
        return homeworkService.getAllType();
    }

    @PostMapping("question-type-add")
    public Boolean createType(@RequestBody NewQuestionDTO questionType) {
        return homeworkService.createType(questionType.getTid(),
                questionType.getType());
    }

    @PostMapping("question-type-delete")
    public Boolean deleteType(@RequestBody NewQuestionDTO questionType) {
        if (questionType.getId() == 0) {
            return homeworkService.deleteType(questionType.getTid(),
                    questionType.getType());
        } else {
            return homeworkService.deleteType(questionType.getTid(),
                    questionType.getId());
        }
    }

    /*
        学生
     */
    // 查询
    @GetMapping("results-get")
    public List<ResultQuestionDTO> getAResult() {
        return homeworkService.selectAllHK_S();
    }

    @GetMapping("result-get")
    public ResultQuestionDTO getResult(@RequestParam("rid") Integer rid) {
        try {
            return homeworkService.selectHKByRID(rid);
        } catch (Exception ex) {
            return null;
        }
    }

    @GetMapping("result-get-course")
    public List<ResultQuestionDTO> getCourseResult(@RequestParam("cid") Integer cid) {
        try {
            return homeworkService.selectHKByCID_S(cid);
        } catch (Exception ex) {
            return null;
        }
    }

    private void setRes_R(HomeworkOpBO res, Boolean isSuccess, String key, Object value) {
        res.setIsSuccess(isSuccess);
        res.setIsQuestion(false);
        res.setInfo(new HashMap<>(1, 1f) {{
            put(key, value);
        }});
    }

    // 插入
    @PostMapping("result-create")
    public HomeworkOpBO insertResult(@RequestBody NewResultDTO newResult) {
        HomeworkOpBO res = new HomeworkOpBO();
        try {
            HomeworkOpBO resultAdd = homeworkService.answerQuestion(
                    newResult.getUid(),
                    newResult.getCid(),
                    newResult.getQid(),
                    newResult.getResult());
            if (!resultAdd.getIsSuccess()) {
                throw new Exception("回答创建失败");
            }
            logger.info("创建回答完成");
            setRes_R(res, true, "rid",
                    ((ResultQuestionDTO) resultAdd.getInfo().get("新建Result")).getResult().getId());
        } catch (Exception ex) {
            logger.error("回答创建异常: {}", ex.getMessage());
            setRes_R(res, false, "Cause", ex.getCause());
        }
        return res;
    }

    // 更新
    @PostMapping("result-update")
    public HomeworkOpBO updateResult(@RequestBody NewResultDTO updateResult) {
        HomeworkOpBO res = new HomeworkOpBO();
        try {
            HomeworkOpBO update
                    = homeworkService.updateResult(updateResult.getUid(), updateResult.getResult());
            if (!update.getIsSuccess()) {
                throw new Exception("问题更新失败");
            }
            logger.info("更新问题完成");
            setRes_R(res, true, "Result",
                    update.getInfo().get("updateResult"));
        } catch (Exception ex) {
            logger.error("更新问题异常: {}", ex.getMessage());
            setRes_R(res, false, "Cause", ex.getCause());
        }
        return res;
    }

    // 删除
    @PostMapping("result-delete")
    public HomeworkOpBO deleteResult(@RequestBody NewResultDTO deleteResult) {
        HomeworkOpBO res = new HomeworkOpBO();
        try {
            HomeworkOpBO delete
                    = homeworkService.deleteResult(deleteResult.getUid(), deleteResult.getQid());
            if (!delete.getIsSuccess()) {
                throw new Exception("删除问题失败");
            }
            logger.info("问题删除完成");
            setRes_R(res, true, "Result",
                    delete.getInfo().get("srcResult"));
        } catch (Exception ex) {
            logger.error("删除问题异常: {}", ex.getMessage());
            setRes_R(res, false, "Cause", ex.getCause());
        }
        return res;
    }
}
